package ru.startandroid.finstack.data.repository

import androidx.lifecycle.LiveData
import ru.startandroid.finstack.data.db.CategoryDao
import ru.startandroid.finstack.data.model.Category

/**
 * Repository for handling Category data operations.
 */
class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun insertAll(categories: List<Category>) {
        categoryDao.insertCategories(categories)
    }

    suspend fun update(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun delete(category: Category) {
        categoryDao.deleteCategory(category)
    }

    suspend fun deleteById(id: Long) {
        categoryDao.deleteCategoryById(id)
    }

    suspend fun deleteAll() {
        categoryDao.deleteAllCategories()
    }

    fun getCategoryById(id: Long): LiveData<Category> {
        return categoryDao.getCategoryById(id)
    }

    fun getCategoriesByType(isExpense: Boolean): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(isExpense)
    }

    fun searchCategories(query: String): LiveData<List<Category>> {
        return categoryDao.searchCategories(query)
    }

    suspend fun getCategoryCount(): Int {
        return categoryDao.getCategoryCount()
    }
} 