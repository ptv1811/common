package com.ltvan.common.task

import kotlinx.coroutines.CoroutineScope

/**
 * @author ltvan@fossil.com
 * on 2023-08-05
 *
 * <p>
 * </p>
 */
open class Task<V : Any> : Promise<V, Error>() {
    /**
     * Add action to be done when the specified task linked with this [Task]
     * completes successfully. This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnSuccess
     *
     * @return  this [Task]
     */
    override fun onSuccess(actionOnSuccess: ActionOnSuccess<V>): Task<V> {
        return super.onSuccess(actionOnSuccess) as Task<V>
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [Task] completes successfully.
     *
     * @param   actionOnSuccess action to be invoked once this [Task] is completed successfully.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnSuccess] will be
     * invoked.
     *
     * @return  this [Task]
     */
    override fun onSuccess(
        actionOnSuccess: ActionOnSuccess<V>,
        dispatcherContextScope: CoroutineScope
    ): Task<V> {
        return super.onSuccess(actionOnSuccess, dispatcherContextScope) as Task<V>
    }

    /**
     * Add action to be done when the specified task linked with this [Task]
     * completes with error. This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnError
     *
     * @return  this [Task]
     */
    override fun onError(actionOnError: ActionOnError<Error>): Task<V> {
        return super.onError(actionOnError) as Task<V>
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [Task] completes with error.
     *
     * @param   actionOnError   action to be invoked once this [Task] is completed with error.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnError] will be
     * invoked.
     *
     * @return  this [Task]
     */
    override fun onError(
        actionOnError: ActionOnError<Error>,
        dispatcherContextScope: CoroutineScope
    ): Task<V> {
        return super.onError(actionOnError, dispatcherContextScope) as Task<V>
    }

    /**
     * Add action to be done when the specified task linked with this [Task]  completes (even if
     * success or error). This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnFinal
     *
     * @return  this [Task]
     */
    override fun finally(actionOnFinal: ActionOnFinal): Task<V> {
        return super.finally(actionOnFinal) as Task<V>
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [Task] is completed (even if success or error).
     *
     * @param   actionOnFinal   action to be invoked once this [Task] is completed (even if
     * success or error).
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnFinal] will be
     * invoked.
     *
     * @return  this [Task]
     */
    override fun finally(
        actionOnFinal: ActionOnFinal,
        dispatcherContextScope: CoroutineScope
    ): Task<V> {
        return super.finally(actionOnFinal, dispatcherContextScope) as Task<V>
    }

    /**
     * Add action to be done when the specified task linked with this  [Task] is canceled. This
     * action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnCancel   action to be invoked once this [Task] is cancel.
     *
     * @return  this [Task]
     */
    override fun onCancel(actionOnCancel: ActionOnCancel<Error>): Task<V> {
        return super.onCancel(actionOnCancel) as Task<V>
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [Task] is canceled.
     *
     * @param   actionOnCancel   action to be invoked once this [Task] is cancel.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnCancel] will be
     * invoked.
     *
     * @return  this [Task]
     */
    override fun onCancel(
        actionOnCancel: ActionOnCancel<Error>,
        dispatcherContextScope: CoroutineScope
    ): Task<V> {
        return super.onCancel(actionOnCancel, dispatcherContextScope) as Task<V>
    }
}