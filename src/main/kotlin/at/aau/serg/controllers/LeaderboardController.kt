package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {

        val sorted = gameResultService.getGameResults().sortedWith(
            compareByDescending<GameResult> { it.score }
                .thenBy { it.timeInSeconds }
        )

        //rank nicht übergeben = ganzes leaderboard
        if (rank == null) return sorted

        //Status 400
        if (rank <= 0 || rank > sorted.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val index = rank - 1
        val from = maxOf(0, index - 3)
        val to = minOf(sorted.size, index + 4)

        return sorted.subList(from, to)
    }
}