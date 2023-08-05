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
open class Promise<V : Any, E : Any> {
    protected val lock: Any = Any()
    var result: V? = null
        private set
    var error: E? = null
        private set

    /**
     * Indicate whether the specific task linked with this [Promise] is success or not.
     */
    val isSuccess: Boolean
        get() {
            return result != null
        }

    /**
     * Indicate whether the specific task linked with this [Promise] is error or not.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val isError: Boolean
        get() {
            return error != null
        }

    /**
     * Indicate whether the specific task linked with this [Promise] is complete
     * (even of success or error) or not.
     */
    val isDone: Boolean
        get() {
            return isSuccess || isError
        }
    private var isCanceled: Boolean = false
    private var actionOnSuccess: CopyOnWriteArrayList<ActionOnSuccess<V>> =
        CopyOnWriteArrayList()
    private var actionOnError: CopyOnWriteArrayList<ActionOnError<E>> =
        CopyOnWriteArrayList()
    private var actionOnFinal: CopyOnWriteArrayList<ActionOnFinal> =
        CopyOnWriteArrayList()
    private var actionOnCancel: CopyOnWriteArrayList<ActionOnCancel<E>> =
        CopyOnWriteArrayList()

    /**
     * Add action to be done when the specified task linked with this [Promise]
     * completes successfully. This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnSuccess
     *
     * @return  this [Promise]
     */
    open fun onSuccess(actionOnSuccess: ActionOnSuccess<V>): Promise<V, E> {
        synchronized(lock) {
            if (isDone) {
                if (isSuccess) {
                    CoroutineScope(Dispatchers.Default).launch {
                        try {
                            actionOnSuccess.invoke(result!!)
                        } catch (e: Exception) {
                            // Fail silently
                        }
                    }
                } else {
                    // Do nothing
                }
            } else {
                this.actionOnSuccess.add(actionOnSuccess)
            }
        }
        return this
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [Promise] completes successfully.
     *
     * @param   actionOnSuccess action to be invoked once this [Promise] is completed successfully.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnSuccess] will be
     * invoked.
     *
     * @return  this [Promise]
     */
    open fun onSuccess(actionOnSuccess: ActionOnSuccess<V>, dispatcherContextScope: CoroutineScope)
            : Promise<V, E> {
        return onSuccess { value ->
            dispatcherContextScope.launch {
                actionOnSuccess.invoke(value)
            }
        }
    }

    /**
     * Add action to be done when the specified task linked with this [Promise]  completes with
     * error. This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnError
     *
     * @return  this [Promise]
     */
    open fun onError(actionOnError: ActionOnError<E>): Promise<V, E> {
        synchronized(lock) {
            if (isDone) {
                if (isError) {
                    CoroutineScope(Dispatchers.Default).launch {
                        try {
                            actionOnError.invoke(error!!)
                        } catch (e: Exception) {
                            // Fail silently
                        }
                    }
                } else {
                    // Do nothing
                }
            } else {
                this.actionOnError.add(actionOnError)
            }
        }
        return this
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [Promise] completes with error.
     *
     * @param   actionOnError   action to be invoked once this [Promise] is completed with error.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnError] will be
     * invoked.
     *
     * @return  this [Promise]
     */
    open fun onError(actionOnError: ActionOnError<E>, dispatcherContextScope: CoroutineScope)
            : Promise<V, E> {
        return onError { error ->
            dispatcherContextScope.launch {
                actionOnError.invoke(error)
            }
        }
    }

    /**
     * Add action to be done when the specified task linked with this [Promise]  completes (even if
     * success or error). This action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnFinal
     *
     * @return  this [Promise]
     */
    open fun finally(actionOnFinal: ActionOnFinal): Promise<V, E> {
        synchronized(lock) {
            if (isDone) {
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        actionOnFinal.invoke(Unit)
                    } catch (e: Exception) {
                        // Fail silently
                    }
                }
            } else {
                this.actionOnFinal.add(actionOnFinal)
            }
        }
        return this
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [Promise] is completed (even if success or error).
     *
     * @param   actionOnFinal   action to be invoked once this [Promise] is completed (even if
     * success or error).
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnFinal] will be
     * invoked.
     *
     * @return  this [Promise]
     */
    open fun finally(actionOnFinal: ActionOnFinal, dispatcherContextScope: CoroutineScope)
            : Promise<V, E> {
        return finally { result ->
            dispatcherContextScope.launch {
                actionOnFinal.invoke(result)
            }
        }
    }

    /**
     * Add action to be done when the specified task linked with this  [Promise] is canceled. This
     * action will be invoked on the [Dispatchers.Default].
     *
     * @param   actionOnCancel   action to be invoked once this [Promise] is cancel.
     *
     * @return  this [Promise]
     */
    open fun onCancel(actionOnCancel: ActionOnCancel<E>): Promise<V, E> {
        synchronized(lock) {
            if (!isDone && !isCanceled) {
                this.actionOnCancel.add(actionOnCancel)
            }
        }
        return this
    }

    /**
     * Add action to be done on specific [CoroutineScope] when the specified task linked with this
     * [Promise] is canceled.
     *
     * @param   actionOnCancel   action to be invoked once this [Promise] is cancel.
     * @param   dispatcherContextScope  the [CoroutineScope] on which the [actionOnCancel] will be
     * invoked.
     *
     * @return  this [Promise]
     */
    open fun onCancel(actionOnCancel: ActionOnCancel<E>, dispatcherContextScope: CoroutineScope)
            : Promise<V, E> {
        return onCancel { error ->
            dispatcherContextScope.launch {
                actionOnCancel.invoke(error)
            }
        }
    }

    fun resolve(result: V) {
        val actionOnSuccessCopies = ArrayList<ActionOnSuccess<V>>()
        val actionOnFinalCopies = ArrayList<ActionOnFinal>()
        synchronized(lock) {
            if (!isDone) {
                this.result = result
                this.error = null
                actionOnSuccessCopies.addAll(this.actionOnSuccess)
                actionOnFinalCopies.addAll(this.actionOnFinal)
                clearCallbacks()
            }
        }
        actionOnSuccessCopies.forEach { action ->
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    action.invoke(result)
                } catch (e: Exception) {
                    // Fail silently
                }
            }
        }
        actionOnFinalCopies.forEach { action ->
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    action.invoke(Unit)
                } catch (e: Exception) {
                    // Fail silently
                }
            }
        }
    }

    fun reject(error: E) {
        val actionOnErrorCopies = ArrayList<ActionOnError<E>>()
        val actionOnFinalCopies = ArrayList<ActionOnFinal>()
        synchronized(lock) {
            if (!isDone) {
                this.error = error
                this.result = result
                actionOnErrorCopies.addAll(this.actionOnError)
                actionOnFinalCopies.addAll(this.actionOnFinal)
                clearCallbacks()
            }
        }
        actionOnErrorCopies.forEach { action ->
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    action.invoke(error)
                } catch (e: Exception) {
                    // Fail silently
                }
            }
        }
        actionOnFinalCopies.forEach { action ->
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    action.invoke(Unit)
                } catch (e: Exception) {
                    // Fail silently
                }
            }
        }
    }

    protected open fun clearCallbacks() {
        // Clear all callbacks
        actionOnSuccess.clear()
        actionOnError.clear()
        actionOnFinal.clear()
        actionOnCancel.clear()
    }

    /**
     * Cancel the specified task linked with this [Promise].
     * [ActionOnError] will be invoked when the specified task completed.
     * In case the specified task has already completed or it is being canceled, nothing will
     * be done.
     *
     * @param   error
     */
    fun cancel(error: E) {
        synchronized(lock) {
            if (!isDone && !isCanceled) {
                isCanceled = true
                if (actionOnCancel.isNotEmpty()) {
                    this.actionOnCancel.forEach { action ->
                        CoroutineScope(Dispatchers.Default).launch {
                            try {
                                action.invoke(error)
                            } catch (e: Exception) {
                                // Fail silently
                            }
                        }
                    }
                } else {
                    reject(error)
                }
            }
        }
    }
}

typealias ActionOnSuccess<V> = ((result: V) -> Unit)
typealias ActionOnError<E> = ((error: E) -> Unit)
typealias ActionOnFinal = ((Unit) -> Unit)
typealias ActionOnCancel<E> = ((error: E) -> Unit)