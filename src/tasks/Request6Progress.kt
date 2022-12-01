package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    val allUsers = mutableListOf<User>()
    repos.map { repo ->
        val repoUsers = service.getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
                .aggregate()
        allUsers.addAll(repoUsers)
        updateResults.invoke(allUsers.aggregate(), false)
    }
    updateResults.invoke(allUsers.aggregate(), true)
}
