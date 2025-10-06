# RepoScore - The GitHub Repository Scoring API

## About

This project provides a REST API endpoint that fetches public GitHub repositories and assigns a **popularity score** to each one. Users can filter repositories by:

- **Programming language**
- **Earliest creation date**

The project is built using **Spring Boot** and runs on **Java 21**.

---

## Score Calculation

The score is a decimal value ranging from **0 to 1**, indicating the relative popularity of a repository. A higher score means a more popular repository.

### Score Components

The final score is calculated based on the following factors:

| Factor             | Weight |
|--------------------|--------|
| Number of Stars    | 0.4    |
| Number of Forks   | 0.4    |
| Recency of Updates | 0.2    |

> **Formula**:  
> `score = (normalizedStars * 0.4) + (normalizedForks * 0.4) + (recencyDecay * 0.2)`

---

### Normalization of Stars and Forks

To ensure fairness across repositories with vastly different star and fork counts, we **normalize** these values using the following formula:

> `normalizedValue = log(1 + value) / log(1 + maxValue)`


This prevents repositories with extremely high star or fork counts from disproportionately skewing the results. 
Without normalization, popular repos would get scores close to 1, while most others would get near-zero scores, making the comparison meaningless.

---

### Recency Decay

To account for how recently a repository was updated, we apply an **exponential decay**:

> `recencyDecay = exp(-ALPHA * hoursSinceLastUpdate)`


Here, `ALPHA = 0.1`, which controls how fast the decay happens. A smaller alpha means slower decay. 
This ensures that **recently updated** repositories score higher.

---

## How to Run the project

This is a **Spring Boot** project using **Java 21**. You can run it using your preferred IDE.

### Steps:

1. Clone the repository.
2. Open it in your IDE.
3. Run the application.
4. The server will start at: `http://localhost:8080`

### 🔗 Endpoint

**GET** `/scoring?language=${language}&earliestCreationDate=${creationDate}`

This endpoint returns a list of repositories with their calculated scores.

#### Optional Query Parameters:

- `language` – Filter by programming language
- `earliestCreatedDate` – Filter by creation date after this value (ISO format, e.g. `2023-01-01`)

If no parameters are provided, it fetches all available repositories (up to GitHub’s API limit).

---

##  Considerations

- GitHub’s API returns a maximum of 1000 repositories.
- The GitHub’s API is rate-limited to 30 requests per minute, exceeding this limit may result in errors (can happen if you call scoring endpoint many times consecutively).
- Fetching all pages from GitHub may take ~20 seconds.
- To optimize performance, a simple in-memory cache (using [Caffeine](https://github.com/ben-manes/caffeine)) stores results for 1 hour. Repeated calls with the same parameters will return cached results.

---

## Possible Improvements

- **Better Error Handling**: Currently, API error codes from GitHub are not handled, and query parameter validation is not implemented.
- **Persistent Caching**: Replace in-memory cache with a more robust solution (e.g., Redis). A background job could refresh repository scores hourly.

---

## About the Use of AI

I used AI (specifically ChatGPT) to assist me with some parts of this project:

- Structuring and formatting this documentation into a proper `.md` file.
- Deciding on how to implement recency decay, since dates couldn’t be normalized the same way as numeric values like stars and forks.
- Choosing a simple in-memory cache (it’s my first time using Caffeine, originally I planned to use Spring’s default in-memory cache, but it didn’t seem to support TTL out of the box).
- Assisting in building the factory classes used in my tests.