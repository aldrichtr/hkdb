[Flags()] enum Modifier {
    None   = 0x0000
    Alt    = 1 -shl 8 # 0x0100
    Shift  = 1 -shl 9 # 0x0200
    Ctrl   = 1 -shl 10 # 0x0400
    Win    = 1 -shl 11 # 0x0800
}
