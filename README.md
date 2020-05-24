# Auth-Server-OAuth2-SpringBoot-2.1.9

GetBasic Auth Token:
-----------------------
curl -X GET "http://localhost:8080/auth-service/secret?clientId=test&clientSecret=temp" -H "accept: */*"

GetPasswordEncodedValue{BCrypt}:
----------------------------
curl -X GET "http://localhost:8080/auth-service/encode?rawPassword=realspeed" -H "accept: */*"

GenerateAccessToken:
----------------------
curl -X POST "http://localhost:8080/auth-service/token" -H "accept: */*" 
  -H "x-auth-client-secret: Basic dGVzdDp0ZW1w" -H "Content-Type: application/json" 
  -d "{ \"grant_type\": \"password\", \"password\": \"admin\", \"username\": \"admin\"}"
  
RefreshToken
----------------
curl -X GET "http://localhost:8080/auth-service/refresh-token?grant_type=refresh_token&refresh_token=refreshToken" 
  -H "accept: */*" -H "x-auth-client-secret: Basic dGVzdDp0ZW1w"

RevokeToken
---------------
curl -X DELETE "http://localhost:8080/auth-service/revoke?access_token=access_token" 
  -H "accept: */*" -H "x-auth-client-secret: Basic dGVzdDp0ZW1w"
