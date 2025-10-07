# RepoScore - The GitHub Repository Scoring API

## About

This project provides a REST API endpoint that fetches public GitHub repositories and assigns a **popularity score** to each one. Users can filter repositories by:

- **Programming language**
- **Earliest creation date**

The project is built using **Spring Boot** and runs on **Java 21**.

---

## Score Calculation

The score is a decimal value that indicates the popularity of a repository. A higher score means a more popular repository.

### Score Components

The final score is calculated based on the following factors:

| Factor             | Weight |
|--------------------|--------|
| Number of Stars    | 0.4    |
| Number of Forks   | 0.4    |
| Recency of Updates | 0.2    |

> **Formula**:  
> `score = (starsScore * 0.4) + (forksScore * 0.4) + (recencyScore * 0.2)`

---

### Score of Stars and Forks

In the end I decided to use simple logarithmic scaling, to score the star counts in a simple way and that rewards popularity but avoids letting high star numbers dominate the score (each additional star adds a bit less than the one before).
We need to sum +1 to the number of stars to avoid log(0) - infinity.

> `starScore = log(1 + numberOfStars) `

Fork score is calculated in the same way using numberOfForks.

Ideally, we should normalize the scores to prevent repositories with extremely high star or fork counts from disproportionately skewing the results. 
I have tried to implement the normalization but afterwards I decided to go for a interface approach and removed it (because for normalization we need the maximum value among all the repos and it was increasing the complexity with the approach I chose). 
But I would suggest it in the improvements section :) 

---

### Recency Decay

To account for how recently a repository was updated, we apply an **exponential decay**:

> `recencyDecay = exp(-ALPHA * hoursSinceLastUpdate)`


Here, `ALPHA = 0.1`, which controls how fast the decay happens. A smaller alpha means slower decay. 
This ensures that recently updated repositories score higher.

---

## How to Run the project

This is a **Spring Boot** project using **Java 21**. You can run it using your preferred IDE.

### Steps:

1. Clone the repository.
2. Open it in your IDE.
3. Run the application.
4. The server will start at: `http://localhost:8080`

### 🔗 Endpoint

**GET** `/scoring?language=${language}&earliestCreatedDate=${creationDate}`

e.g.: http://localhost:8080/scoring?language=java&earliestCreatedDate=2023-01-01

This endpoint returns a list of repositories with their calculated scores.

#### Optional Query Parameters:

- `language` – Filter by programming language
- `earliestCreatedDate` – Filter by creation date from this value (ISO format, e.g. `2023-01-01`)

If no parameters are provided, it fetches all available repositories (up to GitHub’s API limit).

---

##  Considerations

- I used Java 21 because is the version I most used after Java 11 (I thought Java 11 was a bit too old to use here :P), and the one I have more familiarity.
- As GitHub Search API returns many repository properties, I choose to keep only the ones that were more interesting for the problem - and possible to the final user.
- GitHub’s API returns a maximum of 1000 repositories.
- The GitHub’s API is rate-limited to 30 requests per minute, exceeding this limit may result in errors (can happen if you call scoring endpoint many times consecutively).
- Fetching all pages from GitHub may take ~20 seconds.
- To optimize performance, a simple in-memory cache (using [Caffeine](https://github.com/ben-manes/caffeine)) stores results for 1 hour. Repeated calls with the same parameters will return cached results.

---

## Possible Improvements

- **Better Error Handling**: Currently, API error codes from GitHub are handled with a general custom exception, and query parameter validation is not implemented, 
instead I'm only catching the InvalidArgumentException so the user can have at least one feedback. Error handling can be more robust.
- **More robust Caching**: Replace in-memory cache with a more robust solution (e.g., Redis). A background job could refresh repository scores hourly.
- **Ranking repositories** - it was not in the requirements but we could return the repositories ordered by score (highest to lowest)
- **Score normalization** - as I mentioned in the section 'Score of Stars and Forks', I implemented it in the beginning but decided to remove it after thinking more about the design and deciding to go for a interface approach. But is would be a nice feature to have :)
---

## About the Use of AI

I used AI (specifically ChatGPT) to assist me with some parts of this project:

- Structuring and formatting this documentation into a proper `.md` file.
- Deciding on how to implement recency decay, since dates couldn’t be normalized the same way as numeric values like stars and forks.
- Choosing a simple in-memory cache (it’s my first time using Caffeine, originally I planned to use Spring’s default in-memory cache, but it didn’t seem to support TTL out of the box).
- Assisting in building the factory classes used in my tests.