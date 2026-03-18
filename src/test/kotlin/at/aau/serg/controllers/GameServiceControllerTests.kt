package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameResultControllerTests {

    private lateinit var service: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        service = GameResultService()
        controller = GameResultController(service)
    }

    @Test
    fun test_add_and_getGameResult() {
        val gr = GameResult(0, "p", 10, 10.0)

        controller.addGameResult(gr)
        val res = controller.getGameResult(1)

        assertEquals("p", res?.playerName)
    }

    @Test
    fun test_getAllGameResults() {
        controller.addGameResult(GameResult(0, "p1", 10, 10.0))
        controller.addGameResult(GameResult(0, "p2", 20, 20.0))

        val res = controller.getAllGameResults()

        assertEquals(2, res.size)
    }

    @Test
    fun test_deleteGameResult() {
        controller.addGameResult(GameResult(0, "p", 10, 10.0))

        controller.deleteGameResult(1)

        val res = controller.getAllGameResults()
        assertEquals(0, res.size)
    }
}