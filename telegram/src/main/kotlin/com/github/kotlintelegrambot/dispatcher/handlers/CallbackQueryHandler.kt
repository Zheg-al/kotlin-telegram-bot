package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Update

data class CallbackQueryHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val callbackQuery: CallbackQuery
)

data class CallbackQueryResponse(
    val text: String? = null,
    val showAlert: Boolean? = null,
    val url: String? = null,
    val cacheTime: Int? = null,
)

internal class CallbackQueryHandler(
    private val callbackData: String? = null,
    private val handleCallbackQuery: HandleCallbackQuery
) : Handler {

    override fun checkUpdate(update: Update): Boolean {
        val data = update.callbackQuery?.data
        return when {
            data == null -> false
            callbackData == null -> true
            else -> data.split(' ').firstOrNull().equals(callbackData, ignoreCase = true)
        }
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.callbackQuery)
        val callbackQueryHandlerEnv = CallbackQueryHandlerEnvironment(
            bot,
            update,
            update.callbackQuery
        )
        val response = handleCallbackQuery(callbackQueryHandlerEnv)

        val callbackQueryId = update.callbackQuery.id
        bot.answerCallbackQuery(
            callbackQueryId = callbackQueryId,
            text = response?.text,
            showAlert = response?.showAlert,
            url = response?.url,
            cacheTime = response?.cacheTime,
        )
    }
}
