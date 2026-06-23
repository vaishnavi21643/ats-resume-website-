import json
import re
from dataclasses import dataclass

import numpy as np

from ragbase.model import create_embeddings, create_llm

# Embeddings are stateless/cheap to construct (FastEmbedEmbeddings just loads
# a local ONNX model), so building one instance at import time is fine and
# avoids re-loading the model on every request.
_embeddings = create_embeddings()


@dataclass
class MatchResult:
    score: float
    explanation: str


def _cosine_similarity(vec_a: list[float], vec_b: list[float]) -> float:
    a = np.array(vec_a, dtype=float)
    b = np.array(vec_b, dtype=float)
    denom = np.linalg.norm(a) * np.linalg.norm(b)
    if denom == 0:
        return 0.0
    return float(np.dot(a, b) / denom)


def _embedding_score(resume_text: str, job_description: str) -> float:
    """Cosine similarity between resume and job description, scaled to 0-100."""
    vectors = _embeddings.embed_documents([resume_text, job_description])
    similarity = _cosine_similarity(vectors[0], vectors[1])
    # Cosine similarity for sentence embeddings is typically in [0, 1] for
    # related text, occasionally slightly negative for unrelated text.
    # Clamp and scale to a friendlier 0-100 range.
    similarity = max(0.0, min(1.0, similarity))
    return round(similarity * 100, 2)


_EXPLANATION_PROMPT = """You are a recruiting assistant. Compare the candidate's resume \
to the job description below and explain how well the candidate matches the role.

Be specific: mention skills, experience, or qualifications that align, and call out \
any notable gaps. Keep it concise (3-5 sentences).

Respond with ONLY a JSON object in this exact shape, no markdown fences, no extra text:
{{"explanation": "<your explanation here>"}}

JOB DESCRIPTION:
{job_description}

RESUME:
{resume_text}
"""


def _llm_explanation(resume_text: str, job_description: str) -> str:
    llm = create_llm()
    prompt = _EXPLANATION_PROMPT.format(
        job_description=job_description.strip(),
        resume_text=resume_text.strip(),
    )
    response = llm.invoke(prompt)
    content = response.content if hasattr(response, "content") else str(response)

    # Try to parse the JSON the model was asked to produce; fall back to raw
    # text if it didn't comply exactly (models sometimes wrap in ```json).
    cleaned = re.sub(r"^```(?:json)?|```$", "", content.strip(), flags=re.MULTILINE).strip()
    try:
        parsed = json.loads(cleaned)
        return parsed.get("explanation", cleaned)
    except json.JSONDecodeError:
        return cleaned


def compute_match(resume_text: str, job_description: str) -> MatchResult:
    if not resume_text or not resume_text.strip():
        raise ValueError("resume_text must not be empty")
    if not job_description or not job_description.strip():
        raise ValueError("job_description must not be empty")

    score = _embedding_score(resume_text, job_description)
    explanation = _llm_explanation(resume_text, job_description)

    return MatchResult(score=score, explanation=explanation)
