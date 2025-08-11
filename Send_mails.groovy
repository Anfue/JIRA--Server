import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.Group

def issue = Issues.getByKey('HDEV-420399')

def customFieldManager = ComponentAccessor.customFieldManager

// Campos
def cfValidacionesReq = customFieldManager.getCustomFieldObject("customfield_10743")
def cfDevTeam = customFieldManager.getCustomFieldObject("customfield_10304")
def cfbusinessOwner = customFieldManager.getCustomFieldObject("customfield_10300")
// Valores de campos
def valValidacionesReq = issue.getCustomFieldValue(cfValidacionesReq)
def valDevTeam = issue.getCustomFieldValue(cfDevTeam)
def valBusinesOwner = issue.getCustomFieldValue(cfbusinessOwner)

log.warn("Validaciones requeridas → ${valValidacionesReq}")
log.warn("Development Team → ${valDevTeam}")

// 1. Comprobar si "Validaciones requeridas" contiene "Participa Arquitectura" o ID 17209
def participaArquitectura = false
if (valValidacionesReq) {
    if (valValidacionesReq instanceof Collection) {
        participaArquitectura = valValidacionesReq.any { opt ->
            def valor = opt?.toString()?.trim()
            def id = opt?.optionId?.toString()
            valor == "Participa Arquitectura" || id == "17209"
        }
    } else {
        def valor = valValidacionesReq?.toString()?.trim()
        def id = valValidacionesReq?.optionId?.toString()
        participaArquitectura = (valor == "Participa Arquitectura" || id == "17209")
    }
}

// 2. Comprobar grupo válido
def grupoValido = false
if (valDevTeam) {
    if (valDevTeam instanceof Collection) {
        grupoValido = valDevTeam.any { it.name in ["grp_arquitectura", "Engineering_Solutions"] }
    } else {
        grupoValido = valDevTeam.name in ["grp_arquitectura", "Engineering_Solutions"]
    }
}

log.warn("Participa Arquitectura: ${participaArquitectura}, Grupo válido: ${grupoValido}, y valDevTeam.name = ${valDevTeam.name}")

// Enviar email a BO y Security
if (participaArquitectura && grupoValido){
/*
    // Enviar email si grp_arquitectura  se cumplen
     if (valDevTeam.name.contains('grp_arquitectura')) {
        Mail.send {
            setTo("ediezeqt@gmail.com") //CREMADESJ@bancsabadell.com
            setSubject("Aviso: ${issue.key}")
            setBody("""\
            Se ha generado la Tarea: ${issue.key} correspondiente a Arquitectura y tiene tareas pendientes de realizar.”
            """)
        }
        log.warn("Email enviado")
    // Enviar email si Engineering_Solutions se cumplen
        } else if (participaArquitectura && grupoValido && valDevTeam.name.contains('Engineering_Solutions')) {
        Mail.send {
            setTo("ediezeqt@gmail.com") //IBARRAG@sabadelldigital.com
            setSubject("Aviso: ${issue.key}")
            setBody("""\
            Se ha generado la Tarea: ${issue.key} correspondiente a Engineering Solutions y tiene tareas pendientes de realizar.”
            """)
        }
        log.warn("Email enviado")
    } else {
        log.warn("Condiciones no cumplidas, no se envía email")
    }
*/
}
