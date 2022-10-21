/*******************************************************************************/
/*  Generar HTML Mejoras Costes y Beneficios PPR                               */
/*												   		  	                   */
/*	Autor: jfernafu  	     							 	                   */
/*******************************************************************************/
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.Issue

def issue = ComponentAccessor.issueManager.getIssueObject("PPR-8689")

def cfm = ComponentAccessor.getCustomFieldManager()

//PRE

long HTMLcostesID = 16707
long HTMLbeneficiosID = 16708

//long importeBolsaID = 16701
//long proveedorID = 16702
//long tipoBolsaID = 16703
long VariacionID = 16616
long cursoCostesID =  16618
long cursoBeneficiosID = 16705
long SiguiengeCostesID = 16619
long SiguienteBeneficiosID = 16706

//PRO
/*
long HTMLcostesID = 16707
long HTMLbeneficiosID = 16708

long importeBolsaID = 16701
long proveedorID = 16702
long tipoBolsaID = 16703
long VariacionID = 16616
long cursoCostesID =  16618
long cursoBeneficiosID = 16705
long SiguiengeCostesID = 16619
long SiguienteBeneficiosID = 16706	
*/

def variacion = issue.getCustomFieldValue(cfm.getCustomFieldObject(VariacionID)) != null? issue.getCustomFieldValue(cfm.getCustomFieldObject(VariacionID)).toString() +" " + ((Issue) issue.getCustomFieldValue(cfm.getCustomFieldObject(VariacionID))).summary  : ""
//def proveedor = issue.getCustomFieldValue(cfm.getCustomFieldObject(proveedorID)) != null? issue.getCustomFieldValue(cfm.getCustomFieldObject(proveedorID)).toString() : ""
//def importeBolsa = issue.getCustomFieldValue(cfm.getCustomFieldObject(importeBolsaID)) != null? issue.getCustomFieldValue(cfm.getCustomFieldObject(importeBolsaID)).toString() : ""
//def tipoBolsa = issue.getCustomFieldValue(cfm.getCustomFieldObject(tipoBolsaID)) != null? issue.getCustomFieldValue(cfm.getCustomFieldObject(tipoBolsaID)).toString() : ""

def cursoCostes = issue.getCustomFieldValue(cfm.getCustomFieldObject(cursoCostesID)) != null? issue.getCustomFieldValue(cfm.getCustomFieldObject(cursoCostesID)).toString() : ""
def cursoBeneficios = issue.getCustomFieldValue(cfm.getCustomFieldObject(cursoBeneficiosID)) != null? issue.getCustomFieldValue(cfm.getCustomFieldObject(cursoBeneficiosID)).toString() : ""
def siguienteCostes =  issue.getCustomFieldValue(cfm.getCustomFieldObject(SiguiengeCostesID)) != null? issue.getCustomFieldValue(cfm.getCustomFieldObject(SiguiengeCostesID)).toString() : ""
def SiguienteBeneficios = issue.getCustomFieldValue(cfm.getCustomFieldObject(SiguienteBeneficiosID)) != null? issue.getCustomFieldValue(cfm.getCustomFieldObject(SiguienteBeneficiosID)).toString() : ""
log.warn('cursoCostes-->'+cursoCostes)
log.warn('cursoBeneficios-->'+cursoBeneficios)
log.warn('siguienteCostes-->'+siguienteCostes)
log.warn('SiguienteBeneficios-->'+SiguienteBeneficios)


String bolsaHTML = """
{html}
 <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width:95%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
       
		
		 <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
			<td> <b>Variación Mejoras Técnicas (K</b>)</td>
			<td> ${variacion}</td>
            <td >&nbsp; &nbsp; &nbsp;</td >
			<td> <b>Año en Curso</b></td>
			<td> ${cursoCostes} </td>
            <td >&nbsp; &nbsp; &nbsp;</td >
			<td> <b>Siguiente</b> </td>
			<td> ${siguienteCostes}</td>
		</tr>
		 </tbody>
		</table>
{html}
"""



cfm.getCustomFieldObject(HTMLcostesID).updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cfm.getCustomFieldObject(HTMLcostesID)), bolsaHTML),new DefaultIssueChangeHolder());


