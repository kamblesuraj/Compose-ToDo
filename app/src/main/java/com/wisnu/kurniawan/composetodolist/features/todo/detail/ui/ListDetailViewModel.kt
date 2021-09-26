package com.wisnu.kurniawan.composetodolist.features.todo.detail.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wisnu.kurniawan.composetodolist.features.todo.detail.data.IListDetailEnvironment
import com.wisnu.kurniawan.composetodolist.foundation.extension.selectedColor
import com.wisnu.kurniawan.composetodolist.foundation.extension.toColor
import com.wisnu.kurniawan.composetodolist.foundation.extension.toToDoColor
import com.wisnu.kurniawan.composetodolist.foundation.extension.update
import com.wisnu.kurniawan.composetodolist.foundation.viewmodel.StatefulViewModel
import com.wisnu.kurniawan.composetodolist.model.ToDoList
import com.wisnu.kurniawan.composetodolist.runtime.navigation.ARG_LIST_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    listDetailEnvironment: IListDetailEnvironment,
) : StatefulViewModel<ListDetailState, ListDetailEffect, ListDetailAction, IListDetailEnvironment>(ListDetailState(), listDetailEnvironment) {

    val listId = savedStateHandle.get<String>(ARG_LIST_ID)

    init {
        viewModelScope.launch {
            if (listId.isNullOrBlank()) {
                setEffect(ListDetailEffect.ShowCreateListInput)
            } else {
                environment.getListWithTasksById(listId)
                    .flowOn(environment.dispatcher)
                    .collect {
                        setState {
                            setAllState(it)
                        }
                    }
            }
        }
    }

    override fun dispatch(action: ListDetailAction) {
        when (action) {
            is ListDetailAction.ListAction -> handleListAction(action)
            is ListDetailAction.TaskAction -> handleTaskAction(action)
        }
    }

    private fun handleListAction(action: ListDetailAction.ListAction) {
        when (action) {
            is ListDetailAction.ListAction.ApplyColor -> {
                viewModelScope.launch {
                    setState { copy(colors = colors.update(action.color.color)) }
                }
            }
            is ListDetailAction.ListAction.Create -> {
                viewModelScope.launch(environment.dispatcher) {
                    environment.createList(
                        state.value.list.copy(
                            id = environment.idGenerator.generate(),
                            name = state.value.newListName.trim(),
                            color = state.value.colors.selectedColor().toToDoColor()
                        )
                    )
                        .collect {
                            setState {
                                setAllState(it)
                            }
                        }
                }
            }
            is ListDetailAction.ListAction.ChangeName -> {
                viewModelScope.launch {
                    setState { copy(newListName = action.name) }
                }
            }
            ListDetailAction.ListAction.Update -> {
                viewModelScope.launch(environment.dispatcher) {
                    environment.updateList(
                        state.value.list.copy(
                            name = state.value.newListName.trim(),
                            color = state.value.colors.selectedColor().toToDoColor()
                        )
                    ).collect()
                }
            }
            ListDetailAction.ListAction.CancelUpdate -> {
                viewModelScope.launch {
                    setState {
                        copy(
                            newListName = list.name,
                            colors = colors.update(list.color.toColor())
                        )
                    }
                }
            }
            is ListDetailAction.ListAction.InitName -> {
                viewModelScope.launch {
                    setState { copy(list = list.copy(name = action.name), newListName = action.name) }
                }
            }
        }
    }

    private fun handleTaskAction(action: ListDetailAction.TaskAction) {
        when (action) {
            is ListDetailAction.TaskAction.ClickImeDone, ListDetailAction.TaskAction.ClickSubmit -> {
                viewModelScope.launch(environment.dispatcher) {
                    if (state.value.validTaskName) {
                        environment.createTask(state.value.taskName.text.trim(), state.value.list.id)
                        setState { copy(taskName = TextFieldValue()) }
                    }
                }
            }
            is ListDetailAction.TaskAction.ChangeTaskName -> {
                viewModelScope.launch {
                    setState { copy(taskName = action.name) }
                }
            }
            is ListDetailAction.TaskAction.OnShow -> {
                viewModelScope.launch {
                    setState { copy(taskName = taskName.copy(selection = TextRange(taskName.text.length))) }
                }
            }
            is ListDetailAction.TaskAction.OnToggleStatus -> {
                viewModelScope.launch(environment.dispatcher) {
                    environment.toggleTaskStatus(action.task)
                }
            }
            is ListDetailAction.TaskAction.Delete -> {
                viewModelScope.launch(environment.dispatcher) {
                    environment.deleteTask(action.task)
                }
            }
        }
    }

    private fun ListDetailState.setAllState(list: ToDoList) = copy(
        list = list,
        colors = colors.update(list.color.toColor()),
        newListName = list.name
    )

}