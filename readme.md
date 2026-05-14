DeportesUGR - API

# Gestión de usuarios

## Obtener todos los usuarios
`GET /api/usuarios`

## Obtener usuario por ID
`GET /api/usuarios/{id}`

## Crear usuario
`POST /api/usuarios`
### Body
```json
{
    "nombre": "Ana López",
    "email": "ana@mail.com",
    "telefono": "600123123",
    "password": "1234",
    "rol": "USUARIO",
    "especialidad": null
}
```

### Response
```json
{
    "id": 1,
    "nombre": "Ana López",
    "email": "ana@mail.com",
    "telefono": "600123123",
    "password": null,
    "rol": "USUARIO",
    "especialidad": null
}
```
## Eliminar usuario
`DELETE /api/usuarios/{id}`

---

# Gestión de clases

## Obtener clases
`GET /api/clases`

## Obtener clase por ID
`GET /api/clases/{id}`

## Crear clases
### Body
```json
{
  "tipoClase": "TIPO1",
  "fecha": "2026-05-14T10:00:00",
  "especialidad": "FUTBOL",
  "entrenadorId": 1,
  "duracionMinutos": 60,
  "diaSemana": "LUNES",
  "hora": "10:00:00"
}
```

### Response
```json
{
  "tipoClase": "TIPO2",
  "fecha": "2026-05-14T12:00:00",
  "especialidad": "TENIS",
  "entrenadorId": 2,
  "duracionMinutos": 45
}
```
## Eliminar clase
`DELETE /api/clases/{id}`

---

## Eliminar reserva
`DELETE /api/reservas/{id}`

---

# Gestión de bonos

## Obtener bonos
`GET /api/bonos`

## Obtener bono por ID
`GET /api/bonos/{id}`

## Crear bono
`POST /api/bonos`

### Body
```json
{
    "max_usos": 10,
    "tipo": "MENSUAL",
    "usuario": {
        "id": 1
    }
}
```
### Response
```json
{
    "id": 3,
    "maxUsos": 10,
    "tipo": "MENSUAL",
    "usuarioId": 1,
    "usosConsumidos": 0
}
```
## Eliminar bono
`DELETE /api/bonos/{id}`


