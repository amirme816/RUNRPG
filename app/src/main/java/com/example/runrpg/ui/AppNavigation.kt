package com.example.runrpg.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.runrpg.model.Scenarios
import com.example.runrpg.model.ScenarioType
import com.example.runrpg.viewmodel.RunViewModel
import java.util.Calendar

private object Routes {
    const val DASHBOARD = "dashboard"
    const val SCENARIO_SELECT = "scenario_select"
    const val STORY_INTRO = "story_intro"
    const val RUNNING = "running"
    const val STORY_OUTRO = "story_outro"
    const val SUMMARY = "summary"
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val viewModel: RunViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {

        composable(Routes.DASHBOARD) {
            val progress by viewModel.userProgress.collectAsState()
            val lastRun by viewModel.lastRun.collectAsState()
            val history by viewModel.history.collectAsState()

            val weekStart = startOfWeekMillis()
            val weeklyRuns = history.filter { it.startTimeMillis >= weekStart }
            val weeklyDistanceKm = weeklyRuns.sumOf { it.distanceKm }

            DashboardScreen(
                progress = progress,
                weeklyDistanceKm = weeklyDistanceKm,
                weeklyRunCount = weeklyRuns.size,
                lastRun = lastRun,
                onStartClick = { navController.navigate(Routes.SCENARIO_SELECT) }
            )
        }

        composable(Routes.SCENARIO_SELECT) {
            ScenarioSelectScreen(
                onScenarioChosen = { type: ScenarioType ->
                    viewModel.selectScenario(type)
                    navController.navigate(Routes.STORY_INTRO)
                }
            )
        }

        composable(Routes.STORY_INTRO) {
            val scenario = viewModel.selectedScenario.collectAsState().value ?: Scenarios.ZOMBIE_ESCAPE
            StoryScreen(
                scenario = scenario,
                isIntro = true,
                onContinue = {
                    viewModel.startTracking()
                    navController.navigate(Routes.RUNNING)
                }
            )
        }

        composable(Routes.RUNNING) {
            val snapshot by viewModel.trackingSnapshot.collectAsState()
            RunningScreen(
                snapshot = snapshot,
                onPause = { viewModel.pauseTracking() },
                onResume = { viewModel.resumeTracking() },
                onFinish = {
                    viewModel.finishTracking()
                    navController.navigate(Routes.STORY_OUTRO)
                }
            )
        }

        composable(Routes.STORY_OUTRO) {
            val scenario = viewModel.selectedScenario.collectAsState().value ?: Scenarios.ZOMBIE_ESCAPE
            StoryScreen(
                scenario = scenario,
                isIntro = false,
                onContinue = { navController.navigate(Routes.SUMMARY) }
            )
        }

        composable(Routes.SUMMARY) {
            val summary by viewModel.lastSummary.collectAsState()
            summary?.let {
                SummaryScreen(
                    summary = it,
                    onDone = {
                        viewModel.clearSummary()
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

private fun startOfWeekMillis(): Long {
    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.SATURDAY // شروع هفته شمسی/ایرانی
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
