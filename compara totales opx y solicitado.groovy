/*Validacion que indica actividades que no constan en las lineas de coste*/

import com.atlassian.jira.component.ComponentAccessor
import com.opensymphony.workflow.InvalidInputException

// trabajar en consolda
def issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("AJ-248")

def TotalOpexSolicitado = 16005 // Total Opex Solicitado 
def TotalAprobado = 12666 // Total Aprobado € (K)


def cf_TotalOpexSolicitado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(TotalOpexSolicitado)
def cf_TotalAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(TotalAprobado)


def val_TotalOpexSolicitado = issue.getCustomFieldValue(cf_TotalOpexSolicitado)
def val_TotalAprobado = issue.getCustomFieldValue(cf_TotalAprobado)

log.warn('val_TotalOpexSolicitado-->'+val_TotalOpexSolicitado)
log.warn('val_TotalAprobado-->'+val_TotalAprobado)
log.error(val_TotalOpexSolicitado+' > '+val_TotalAprobado)

if(val_TotalOpexSolicitado > val_TotalAprobado){
    throw new InvalidInputException("El valor de la solicitud del año actual debe ser inferior o igual a la aprobada ")
    return false
}else{
    return true
}

