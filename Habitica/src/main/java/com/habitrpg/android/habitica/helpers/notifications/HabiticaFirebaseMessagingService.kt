package com.habitrpg.android.habitica.helpers.notifications

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.habitrpg.android.habitica.HabiticaBaseApplication
import com.habitrpg.android.habitica.components.UserComponent
import com.habitrpg.android.habitica.data.UserRepository
import com.habitrpg.android.habitica.helpers.RxErrorHandler
import javax.inject.Inject

class HabiticaFirebaseMessagingService : FirebaseMessagingService() {

    private val userComponent: UserComponent?
    get() = HabiticaBaseApplication.userComponent

    @Inject
    internal lateinit var pushNotificationManager: PushNotificationManager

    @Inject
    internal lateinit var userRepository: UserRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        userComponent?.inject(this)
        if (this::pushNotificationManager.isInitialized) {
            pushNotificationManager.displayNotification(remoteMessage)

            if (remoteMessage.data["identifier"]?.contains(PushNotificationManager.WON_CHALLENGE_PUSH_NOTIFICATION_KEY) == true) {
                userRepository.retrieveUser(true).subscribe({}, RxErrorHandler.handleEmptyError())
            }
        }
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        userComponent?.inject(this)
        val refreshedToken = FirebaseInstanceId.getInstance().token
        if (refreshedToken != null && this::pushNotificationManager.isInitialized) {
            pushNotificationManager.refreshedToken = refreshedToken
        }
    }
}