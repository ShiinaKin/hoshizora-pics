package io.sakurasou.constant

/**
 * @author Shiina Kin
 * 2024/12/9 08:52
 */

const val REGEX_USERNAME = "^[a-zA-Z0-9]{4,20}\$"
const val REGEX_PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,32}\$"
const val REGEX_EMAIL = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$"
const val REGEX_URL =
    "^https?://(.+\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)\$"
