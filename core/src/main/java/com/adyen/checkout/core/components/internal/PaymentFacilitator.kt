/*
 * Copyright (c) 2025 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by ozgur on 8/4/2025.
 */

package com.adyen.checkout.core.components.internal

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.adyen.checkout.core.action.data.Action
import com.adyen.checkout.core.action.internal.ActionProvider
import com.adyen.checkout.core.components.CheckoutController
import com.adyen.checkout.core.components.internal.ui.PaymentDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class PaymentFacilitator(
    private val paymentDelegate: PaymentDelegate<BaseComponentState>,
    private val coroutineScope: CoroutineScope,
    private val componentEventHandler: ComponentEventHandler<BaseComponentState>,
    private val actionProvider: ActionProvider,
    private val checkoutController: CheckoutController,
) {

    @Composable
    fun ViewFactory(modifier: Modifier = Modifier) {
        paymentDelegate.ViewFactory(modifier)
    }

    fun observe(lifecycle: Lifecycle) {
        paymentDelegate.eventFlow
            .flowWithLifecycle(lifecycle)
            .filterNotNull()
            .onEach { event ->
                componentEventHandler.onPaymentComponentEvent(event)
            }.launchIn(coroutineScope)

        checkoutController.events
            .flowWithLifecycle(lifecycle)
            .onEach { event ->
                when (event) {
                    CheckoutController.Event.Submit -> submit()
                    is CheckoutController.Event.HandleAction -> handleAction(event.action)
                    is CheckoutController.Event.HandleIntent -> handleIntent(event.intent)
                }
            }
            .launchIn(coroutineScope)
    }

    private fun submit() {
        // TODO - what if we are handling an action?
        paymentDelegate.submit()
    }

    private fun handleAction(action: Action) {
        // TODO - Store the actionDelegate
        actionProvider.get(
            action = action,
            coroutineScope = coroutineScope,
        )
    }

    private fun handleIntent(intent: Intent) {
        // TODO - handle intent with action delegate
    }
}
