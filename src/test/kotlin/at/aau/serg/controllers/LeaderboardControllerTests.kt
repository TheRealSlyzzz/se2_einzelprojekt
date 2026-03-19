package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        //Reihenfolge nach Time
        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    //Test for same score sorting
    @Test
    fun test_getLeaderboard_sameScore_sortedByTime() {
        val a = GameResult(1, "a", 50, 30.0)
        val b = GameResult(2, "b", 50, 10.0)
        val c = GameResult(3, "c", 50, 20.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(a, b, c))

        val res = controller.getLeaderboard(null)

        // same score → time ascending
        assertEquals(b, res[0]) // 10s
        assertEquals(c, res[1]) // 20s
        assertEquals(a, res[2]) // 30s
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectIdSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        //Reihenfolge nach Time
        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    //Test wenn kein Rank übergeben wird
    @Test
    fun test_getLeaderboard_noRank_returnsFullList() {
        val a = GameResult(1, "a", 30, 30.0)
        val b = GameResult(2, "b", 20, 20.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(b, a))

        val res = controller.getLeaderboard(null)

        assertEquals(2, res.size)
        assertEquals(a, res[0])
        assertEquals(b, res[1])
    }

    @Test
    fun test_getLeaderboard_withRank_returnsNeighborhood() {
        val list = (1..10).map {
            GameResult(it.toLong(), "p$it", 100 - it, it.toDouble())
        }

        whenever(mockedService.getGameResults()).thenReturn(list)

        val res = controller.getLeaderboard(5)

        // index 4 → von 1 bis 7 (±3)
        assertEquals(7, res.size)
    }

    //Test wenn ungültiger Rank übergeben wird
    @Test
    fun test_getLeaderboard_invalidRank_throwsException() {
        val list = listOf(GameResult(1, "a", 10, 10.0))
        whenever(mockedService.getGameResults()).thenReturn(list)

        try {
            controller.getLeaderboard(0)
        } catch (e: Exception) {
            assert(e is org.springframework.web.server.ResponseStatusException)
            return
        }

        throw AssertionError("Exception expected")
    }

    @Test
    fun test_getLeaderboard_rankAtStart() {
        val list = (1..5).map {
            GameResult(it.toLong(), "p$it", 100 - it, it.toDouble())
        }

        whenever(mockedService.getGameResults()).thenReturn(list)

        val res = controller.getLeaderboard(1)

        assertEquals(4, res.size) // nur nach unten möglich
    }

    @Test
    fun test_getLeaderboard_rankAtEnd() {
        val list = (1..5).map {
            GameResult(it.toLong(), "p$it", 100 - it, it.toDouble())
        }

        whenever(mockedService.getGameResults()).thenReturn(list)

        val res = controller.getLeaderboard(5)

        assertEquals(4, res.size) // nur nach oben möglich
    }

}