package com.example.mandiexe.models.body.authBody


import com.squareup.moshi.Json

data class SignUpBody(

    @Json(name = "address")
    val address: String, // b381, asia
    @Json(name = "area_numerical")
    val area_numerical: Int, // 100
    @Json(name = "area_unit")
    val area_unit: String, // acre
    @Json(name = "country")
    val country: String, // ind
    @Json(name = "district")
    val district: String, // del
    @Json(name = "latitude")
    val latitude: String, // 9.438933
    @Json(name = "longitude")
    val longitude: String, // +2.9404353
    @Json(name = "name")
    val name: String, // kavya vatsal
    @Json(name = "phone")
    val phone: String, // +918585992062
    @Json(name = "state")
    val state: String, // del
    @Json(name = "token")
    val token: String, // eyJhbGciOiJSUzI1NiIsImtpZCI6IjNjYmM4ZjIyMDJmNjZkMWIxZTEwMTY1OTFhZTIxNTZiZTM5NWM2ZDciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vZS1tYW5kaS0zN2UxOSIsImF1ZCI6ImUtbWFuZGktMzdlMTkiLCJhdXRoX3RpbWUiOjE2MDg0OTQxOTcsInVzZXJfaWQiOiJTMnVzdHpBTzRGZUZTZ0NBTmZadE5SbDdpWjQyIiwic3ViIjoiUzJ1c3R6QU80RmVGU2dDQU5mWnROUmw3aVo0MiIsImlhdCI6MTYwODQ5NDE5NywiZXhwIjoxNjA4NDk3Nzk3LCJwaG9uZV9udW1iZXIiOiIrOTE4NTg1OTkyMDYyIiwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJwaG9uZSI6WyIrOTE4NTg1OTkyMDYyIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGhvbmUifX0.GXTyyGP08_zKGqXvFshz2_S9KRey-l3jKcxq1R-TKXydrBGE4tZdVVNRYwgiRnGwk9DXPo-BVMIWq31j2nVDV2dJrkmAMVxKst3wKjX9qMEX861W9x02tBg7o62xaU1711XOpEgd1tjWrqf8fdckhmT6b4q1XJk-Q5QkFit3brVndQ0OdH7a1Ul5DGYE152D3ezVUhsbN1WIeKLNsXV_cA3hueV1ceIFQFhSom7B7Tq1MoaRSAI35nkMQDgRqjE1IsdkRr2cI_Lddb4EkQLHCescJS1IRlAJeimNqMrknyLOMduXbeVV4wD5eRwM42JhxLj0L7lXvH4wunWMgS0eiQ
    @Json(name = "village")
    val village: String // greno
)