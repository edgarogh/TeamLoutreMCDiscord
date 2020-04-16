package fr.edgarogh.tlmcd

/**
 * @param imageAttachments (attachment name, attachment URL)[]
 */
data class RemoteMessage(
    val discordUserId: String,
    val discordUserName: String,
    val content: String,
    val imageAttachments: Set<Pair<String, String>>
)
