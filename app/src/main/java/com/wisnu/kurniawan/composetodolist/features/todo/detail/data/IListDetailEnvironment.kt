package com.wisnu.kurniawan.composetodolist.features.todo.detail.data

import com.wisnu.kurniawan.composetodolist.foundation.wrapper.DateTimeGenerator
import com.wisnu.kurniawan.composetodolist.foundation.wrapper.IdGenerator
import com.wisnu.kurniawan.composetodolist.model.ToDoList
import com.wisnu.kurniawan.composetodolist.model.ToDoTask
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface IListDetailEnvironment {
    val idGenerator: IdGenerator
    val dateTimeGenerator: DateTimeGenerator
    val dispatcher: CoroutineDispatcher
    fun getListWithTasksById(listId: String): Flow<ToDoList>
    suspend fun createList(list: ToDoList): Flow<ToDoList>
    suspend fun updateList(list: ToDoList): Flow<Any>
    suspend fun createTask(taskName: String, listId: String)
    suspend fun toggleTaskStatus(toDoTask: ToDoTask)
    suspend fun deleteTask(task: ToDoTask)
}