package io.sakurasou.constant

/**
 * @author Shiina Kin
 * 2024/9/12 17:31
 */

private const val USER = "user"
private const val GROUP = "group"
private const val ROLE = "role"
private const val PERMISSION = "permission"
private const val SETTING = "setting"
private const val STRATEGY = "strategy"
private const val IMAGE = "image"
private const val ALBUM = "album"
private const val PERSONAL_ACCESS_TOKEN = "personal_access_token"

private const val READ = "read"
private const val WRITE = "write"
private const val DELETE = "delete"

private const val SINGLE = "single"
private const val SELF = "self"
private const val ALL = "all"

const val USER_READ_SELF = "$USER:$READ:$SELF"
const val USER_READ_ALL_SINGLE = "$USER:$READ:$ALL:$SINGLE"
const val USER_READ_ALL_ALL = "$USER:$READ:$ALL:$ALL"
const val USER_WRITE_SELF = "$USER:$WRITE:$SELF"
const val USER_WRITE_ALL = "$USER:$WRITE:$ALL"
const val USER_DELETE = "$USER:$DELETE"
const val USER_BAN = "$USER:ban"

const val GROUP_READ_SINGLE = "$GROUP:$READ:$SINGLE"
const val GROUP_READ_ALL = "$GROUP:$READ:$ALL"
const val GROUP_WRITE = "$GROUP:$WRITE"
const val GROUP_DELETE = "$GROUP:$DELETE"

const val ROLE_READ_SELF = "$ROLE:$READ:$SELF"
const val ROLE_READ_ALL = "$ROLE:$READ:$ALL"
const val ROLE_WRITE_ALL = "$ROLE:$WRITE:$ALL"

const val PERMISSION_READ = "$PERMISSION:$READ"
const val PERMISSION_WRITE = "$PERMISSION:$WRITE"
const val PERMISSION_DELETE = "$PERMISSION:$DELETE"

const val SETTING_READ = "$SETTING:$READ"
const val SETTING_WRITE = "$SETTING:$WRITE"

const val STRATEGY_READ_ALL = "$STRATEGY:$READ:$ALL"
const val STRATEGY_READ_SINGLE = "$STRATEGY:$READ:$SINGLE"
const val STRATEGY_WRITE = "$STRATEGY:$WRITE"
const val STRATEGY_DELETE = "$STRATEGY:$DELETE"

const val IMAGE_READ_SELF_SINGLE = "$IMAGE:$READ:$SELF:$SINGLE"
const val IMAGE_READ_SELF_ALL = "$IMAGE:$READ:$SELF:$ALL"
const val IMAGE_READ_ALL_SINGLE = "$IMAGE:$READ:$ALL:$SINGLE"
const val IMAGE_READ_ALL_ALL = "$IMAGE:$READ:$ALL:$ALL"
const val IMAGE_WRITE_SELF = "$IMAGE:$WRITE:$SELF"
const val IMAGE_WRITE_ALL = "$IMAGE:$WRITE:$ALL"
const val IMAGE_DELETE_SELF = "$IMAGE:$DELETE:$SELF"
const val IMAGE_DELETE_ALL = "$IMAGE:$DELETE:$ALL"

const val ALBUM_READ_SELF_SINGLE = "$ALBUM:$READ:$SELF:$SINGLE"
const val ALBUM_READ_SELF_ALL = "$ALBUM:$READ:$SELF:$ALL"
const val ALBUM_READ_ALL_SINGLE = "$ALBUM:$READ:$ALL:$SINGLE"
const val ALBUM_READ_ALL_ALL = "$ALBUM:$READ:$ALL:$ALL"
const val ALBUM_WRITE_SELF = "$ALBUM:$WRITE:$SELF"
const val ALBUM_WRITE_ALL = "$ALBUM:$WRITE:$ALL"
const val ALBUM_DELETE_SELF = "$ALBUM:$DELETE:$SELF"
const val ALBUM_DELETE_ALL = "$ALBUM:$DELETE:$ALL"

const val PERSONAL_ACCESS_TOKEN_READ_SELF = "$PERSONAL_ACCESS_TOKEN:$READ:$SELF"
const val PERSONAL_ACCESS_TOKEN_WRITE_SELF = "$PERSONAL_ACCESS_TOKEN:$WRITE:$SELF"
