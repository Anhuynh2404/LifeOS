package com.example.core.event

import com.example.core.engine.RuleEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LifeEventBus(private val ruleEngine: RuleEngine) {
    private val _events = MutableSharedFlow<LifeEvent>(replay = 10)
    val events: SharedFlow<LifeEvent> = _events.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    fun postEvent(event: LifeEvent) {
        scope.launch {
            _events.emit(event)
            ruleEngine.processEvent(event)
        }
    }
}
