package com.wisnu.kurniawan.composetodolist.runtime.navigation

import androidx.compose.runtime.MutableState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.wisnu.kurniawan.composetodolist.features.todo.step.ui.CreateStepScreen
import com.wisnu.kurniawan.composetodolist.features.todo.step.ui.RenameStepScreen
import com.wisnu.kurniawan.composetodolist.features.todo.step.ui.RenameTaskScreen
import com.wisnu.kurniawan.composetodolist.features.todo.step.ui.RepeatSelectionScreen
import com.wisnu.kurniawan.composetodolist.features.todo.step.ui.StepScreen
import com.wisnu.kurniawan.composetodolist.features.todo.step.ui.StepViewModel
import com.wisnu.kurniawan.composetodolist.features.todo.step.ui.UpdateTaskNoteScreen

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.StepNavHost(
    navController: NavHostController,
    bottomSheetConfig: MutableState<MainBottomSheetConfig>
) {
    navigation(
        startDestination = StepFlow.TaskDetailScreen.route,
        route = StepFlow.Root.route
    ) {
        composable(
            route = StepFlow.TaskDetailScreen.route,
            arguments = StepFlow.TaskDetailScreen.arguments,
            deepLinks = StepFlow.TaskDetailScreen.deepLinks
        ) {
            val viewModel = hiltViewModel<StepViewModel>()
            StepScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        bottomSheet(StepFlow.CreateStep.route) {
            val viewModel = if (navController.previousBackStackEntry != null) {
                hiltViewModel<StepViewModel>(
                    navController.previousBackStackEntry!!
                )
            } else {
                hiltViewModel()
            }
            bottomSheetConfig.value = NoScrimSmallShapeMainBottomSheetConfig

            CreateStepScreen(viewModel = viewModel)
        }
        bottomSheet(
            route = StepFlow.EditStep.route,
            arguments = StepFlow.EditStep.arguments
        ) { backStackEntry ->
            val viewModel = if (navController.previousBackStackEntry != null) {
                hiltViewModel<StepViewModel>(
                    navController.previousBackStackEntry!!
                )
            } else {
                hiltViewModel()
            }
            val stepId = backStackEntry.arguments?.getString(ARG_STEP_ID)
            bottomSheetConfig.value = DefaultMainBottomSheetConfig

            RenameStepScreen(
                navController = navController,
                viewModel = viewModel,
                stepId = stepId.orEmpty()
            )
        }
        bottomSheet(StepFlow.EditTask.route) {
            val viewModel = if (navController.previousBackStackEntry != null) {
                hiltViewModel<StepViewModel>(
                    navController.previousBackStackEntry!!
                )
            } else {
                hiltViewModel()
            }
            bottomSheetConfig.value = DefaultMainBottomSheetConfig

            RenameTaskScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        bottomSheet(StepFlow.SelectRepeatTask.route) {
            val viewModel = if (navController.previousBackStackEntry != null) {
                hiltViewModel<StepViewModel>(
                    navController.previousBackStackEntry!!
                )
            } else {
                hiltViewModel()
            }
            bottomSheetConfig.value = DefaultMainBottomSheetConfig

            RepeatSelectionScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        bottomSheet(StepFlow.UpdateTaskNote.route) {
            val viewModel = if (navController.previousBackStackEntry != null) {
                hiltViewModel<StepViewModel>(
                    navController.previousBackStackEntry!!
                )
            } else {
                hiltViewModel()
            }
            bottomSheetConfig.value = DefaultMainBottomSheetConfig

            UpdateTaskNoteScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
