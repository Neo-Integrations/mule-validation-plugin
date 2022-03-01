%dw 2.0

import dw::Crypto
import * from dw::core::Binaries

type HashAlgorithm = "MD2" | "MD5" | "SHA1" | "SHA-256" | "SHA-512"

fun genETag(data : String, algo: ALGORITHM) : String = (
    if(isBlank(data)) ""
    else toHex(Crypto::hashWith(data as Binary, algo))
)

fun compareETag(receivedETag : String, data : String, algo : HashAlgorithm) : Boolean = (
    genETag(data, algo) == receivedETag
)