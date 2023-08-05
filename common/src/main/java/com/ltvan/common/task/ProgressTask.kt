package com.ltvan.common.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author ltvan@fossil.com
 * on 2023-08-05
 *
 * <p>
 * </p>
 */
open class ProgressTask<V : Any> : Task<V>() {
    var lastProgress: Float = 0f
        private set
    private var actionOnProgressChange: CopyOnWriteArrayList<ActionOnProgressChange> =
        CopyOnWriteArrayList()

    override fun clearCallbacks() {
        super.clearCallbacks()
        actionOnProgressChange.clear()
    }

    /**
     * Add action to be done when the specified task linked with this [ProgressTask]
     * completes successfully. This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnSuccess
     *
     * @return  this [ProgressTask]
     */
    override fun onSuccess(actionOnSuccess: ActionOnSuccess<V>): ProgressTask<V> {
        return super.onSuccess(actionOnSuccess) as ProgressTask<V>
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [ProgressTask] completes successfully.
     *
     * @param   actionOnSuccess action to be invoked once this [ProgressTask] is completed
     * successfully.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnSuccess] will be
     * invoked.
     *
     * @return  this [ProgressTask]
     */
    override fun onSuccess(
        actionOnSuccess: ActionOnSuccess<V>,
        dispatcherContextScope: CoroutineScope
    ): ProgressTask<V> {
        return super.onSuccess(actionOnSuccess, dispatcherContextScope) as ProgressTask
    }

    /**
     * Add action to be done when the specified task linked with this [ProgressTask]
     * completes with error. This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnError
     *
     * @return  this [ProgressTask]
     */
    override fun onError(actionOnError: ActionOnError<Error>): ProgressTask<V> {
        return super.onError(actionOnError) as ProgressTask<V>
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [ProgressTask] completes with error.
     *
     * @param   actionOnError   action to be invoked once this [ProgressTask] is completed with error.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnError] will be
     * invoked.
     *
     * @return  this [ProgressTask]
     */
    override fun onError(
        actionOnError: ActionOnError<Error>,
        dispatcherContextScope: CoroutineScope
    ): ProgressTask<V> {
        return super.onError(actionOnError, dispatcherContextScope) as ProgressTask
    }

    /**
     * Add action to be done when the specified task linked with this [ProgressTask]  completes
     * (even if success or error). This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnFinal
     *
     * @return  this [ProgressTask]
     */
    override fun finally(actionOnFinal: ActionOnFinal): ProgressTask<V> {
        return super.finally(actionOnFinal) as ProgressTask<V>
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [ProgressTask] is completed (even if success or error).
     *
     * @param   actionOnFinal   action to be invoked once this [ProgressTask] is completed (even if
     * success or error).
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnFinal] will be
     * invoked.
     *
     * @return  this [ProgressTask]
     */
    override fun finally(
        actionOnFinal: ActionOnFinal,
        dispatcherContextScope: CoroutineScope
    ): ProgressTask<V> {
        return super.finally(actionOnFinal, dispatcherContextScope) as ProgressTask
    }

    /**
     * Add action to be done when the specified task linked with this [ProgressTask]
     * updates its progress. This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnProgressChange
     *
     * @return  this [ProgressTask]
     */
    fun onProgressChange(actionOnProgressChange: ActionOnProgressChange):
            ProgressTask<V> {
        synchronized(lock) {
            if (!isDone) {
                this.actionOnProgressChange.add(actionOnProgressChange)
            }
        }
        return this
    }

    /**
     * Add action to be done when the specified task linked with this [ProgressTask]
     * updates its progress.
     *
     * @param   actionOnProgressChange  action to be invoked once this [Promise] updates progress.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnProgressChange]
     * will be invoked.
     *
     * @return  this [ProgressTask]
     */
    fun onProgressChange(
        actionOnProgressChange: ActionOnProgressChange,
        dispatcherContextScope: CoroutineScope
    ): ProgressTask<V> {
        return onProgressChange { progress ->
            dispatcherContextScope.launch {
                actionOnProgressChange.invoke(progress)
            }
        }
    }

    /**
     * Add action to be done when the specified task linked with this  [ProgressTask] is canceled.
     * This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnCancel   action to be invoked once this [ProgressTask] is cancel.
     *
     * @return  this [ProgressTask]
     */
    override fun onCancel(actionOnCancel: ActionOnCancel<Error>): ProgressTask<V> {
        return super.onCancel(actionOnCancel) as ProgressTask<V>
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [ProgressTask] is canceled.
     *
     * @param   actionOnCancel   action to be invoked once this [ProgressTask] is cancel.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnCancel] will be
     * invoked.
     *
     * @return  this [ProgressTask]
     */
    override fun onCancel(
        actionOnCancel: ActionOnCancel<Error>,
        dispatcherContextScope: CoroutineScope
    ): ProgressTask<V> {
        return super.onCancel(actionOnCancel, dispatcherContextScope) as ProgressTask
    }

    fun updateProgress(progress: Float) {
        lastProgress = progress
        this.actionOnProgressChange.forEach { action ->
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    action.invoke(progress)
                } catch (e: Exception) {
                    // Fail silently
                }
            }
        }
    }
}

typealias ActionOnProgressChange = ((progress: Float) -> Unit)