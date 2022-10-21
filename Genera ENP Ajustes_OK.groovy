import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import groovy.transform.Field
import com.atlassian.jira.ComponentManager;
import Helper.CommonHelper
import Helper.BDHelper
import Helper.GridHelper
import Helper.NfeedHelper
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.index.IssueIndexingService

//Constantes y managers
@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()

/************Acceso issue CONSOLA*******************/
def issue = ComponentAccessor.getIssueManager().getIssueObject("AJ-1124")
//IDs campos
def gridID = 18221L //  Verificacion LC finance
def gridIDPEP = 18120L //  tabla PEP
def gridIDMod = 18214L // Modificacion PEP existente
//usuario de accion
def user = ch.getAdminUser()
log.error('USER-->' + user.class)
def pluginAccessor = ComponentAccessor.getPluginAccessor();
changeSetClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.dto.TGEChangeSet");
def gridData = gh.getAllGridRows(issue.id, gridID, user)
def gridDataPEP = gh.getAllGridRows(issue.id, gridIDPEP, user)
def gridDataMod = gh.getAllGridRows(issue.id, gridIDMod, user)
double sumaSoly = 0.0
double ValEnp = 0.0
def Id 
def valueS1=0d;def valueS2=0d;def valueS3=0d;def valueS4=0d;def valueS5=0d;
def value1 = 0d;def value2 = 0d;def value3 = 0d;def value4 = 0d;def value5 = 0d
def VENPOr1 = 0d;def VENPOr2 = 0d;def VENPOr3 = 0d;def VENPOr4 = 0d;def VENPOr5 = 0d
def ID_ENP
def ID_SOF
// valores a comparar:
def cliente; def sscte; def articulos; def prov; def nivelsup; def pdName;
ch.message('Si se han rebajado los importes de software (PEP de tipo SOF), el importe de los PEP de ENP asociados también se ha recalculado.', 1)
gridDataPEP.each() //por cada Linea de Verificacion LC finance
{
    
    valueS1 = (it.soly1 == null ? 0.0 : it.soly1)
	valueS2 = (it.soly2 == null ? 0.0 : it.soly2)
	valueS3 = (it.soly3 == null ? 0.0 : it.soly3)
	valueS4 = (it.soly4 == null ? 0.0 : it.soly4)
	valueS5 = (it.soly5 == null ? 0.0 : it.soly5)
    log.warn(sumaSoly)
	sumaSoly = valueS1+valueS2+valueS3+valueS4+valueS5
    if (it.tipopep.name == 'SOF' && sumaSoly <= 0.0){
		log.error('Tipo de PEP = '+it.tipopep.name+' y el solicitado = '+sumaSoly)
        ID_SOF = it.IDpep
        value1 = it.y1.multiply(0.125)
		value2 = it.y2.multiply(0.125)
		value3 = it.y3.multiply(0.125)
		value4 = it.y4.multiply(0.125)
		value5 = it.y5.multiply(0.125)
        log.error('Id SOF = '+it.IDpep+' y la suma del solicitado es '+sumaSoly)
           gridDataMod.each() //por cada Linea de Modificacion PEP existentes
           {
            log.error(it)
            if (it.tipopep == 'SOF' && ID_SOF == it.nompep) {
                log.error('Aporbado del SOF '+it.nompep+' = '+it.totalAprobado)
                ValEnp = it.totalAprobado.multiply(0.125)
                Id = it.nompep
                cliente=it.cliente;sscte = it.sscte;articulos = it.grupoart; prov= it.prov; nivelsup= it.nivelsup; pdName = it.pd.name
            gridDataMod.each() //por cada Linea de Modificacion PEP existentes
            {
                //buscar valor aprobado por años y si no se calculasn que ponga el original. 

                if (cliente == it.cliente &&  nivelsup == it.nivelsup && pdName == it.pd.name && prov == it.prov && articulos = it.grupoart && sscte == it.sscte && it.tipopep == 'ENP')  {
                    ID_ENP = it.nompep
					VENPOr1 = it.aprobadoAux1;VENPOr2 = it.aprobadoAux2;VENPOr3 = it.aprobadoAux3;VENPOr4 = it.aprobadoAux4;VENPOr5 = it.aprobadoAux5
                    gridDataPEP.each() //por cada Linea de Verificacion LC finance
					{	
                        log.error('it.tipopep.name-->'+it.tipopep.name)
                                                    log.warn(' it.y2-->'+ it.y2);log.error('value2-->'+value2);log.error('VENPOr2-->'+VENPOr2)

                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value1< VENPOr1 ){
                            log.warn(' it.y1-->'+ it.y1);log.error('value1-->'+value1);log.error('VENPOr1-->'+VENPOr1)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 1 de '+value1)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", value1 != null ? value1 : 0)
                            row.put("y2aux", it.y2 != null ? it.y2 : 0)
                            row.put("y3aux", it.y3 != null ? it.y3 : 0)
                            row.put("y4aux", it.y4 != null ? it.y4 : 0)
                            row.put("y5aux", it.y5 != null ? it.y5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP del año 1')
                            gh.updateRowGrid(issue.getId(), gridIDPEP, user, row, it.id)
                        }
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value2 < VENPOr2 ){
                            log.warn(' it.y2-->'+ it.y2);log.error('value2-->'+value2);log.error('VENPOr2-->'+VENPOr2)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 2 de '+value2)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", it.y1 != null ? it.y1 : 0)
                            row.put("y2aux", value2 != null ? value2: 0)
                            row.put("y3aux", it.y3 != null ? it.y3 : 0)
                            row.put("y4aux", it.y4 != null ? it.y4 : 0)
                            row.put("y5aux", it.y5 != null ? it.y5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP del año 2')
                            gh.updateRowGrid(issue.getId(), gridIDPEP, user, row, it.id)
                        }
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value3 < VENPOr3){
                            log.warn(' it.y3-->'+ it.y3);log.error('value3-->'+value3);log.error('VENPOr3-->'+VENPOr3)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 3 de '+value3)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", it.y1 != null ? it.y1 : 0)
                            row.put("y2aux", it.y2 != null ? it.y2 : 0)
                            row.put("y3aux", value3 != null ? value3 : 0)
                            row.put("y4aux", it.y4 != null ? it.y4 : 0)
                            row.put("y5aux", it.y5 != null ? it.y5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP del año 3')
                            gh.updateRowGrid(issue.getId(), gridIDPEP, user, row, it.id)
                        }
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value4 < VENPOr4){
                            log.warn(' it.y4-->'+ it.y4);log.error('value4-->'+value4);log.error('VENPOr4-->'+VENPOr4)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 4 de '+value4)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", it.y1 != null ? it.y1 : 0)
                            row.put("y2aux", it.y2 != null ? it.y2 : 0)
                            row.put("y3aux", it.y3 != null ? it.y3 : 0)
                            row.put("y4aux", value4 != null ? value4 : 0)
                            row.put("y5aux", it.y5 != null ? it.y5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP del año 4')
                            gh.updateRowGrid(issue.getId(), gridIDPEP, user, row, it.id)
                        }
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value5 < VENPOr5){
                            log.warn(' it.y5-->'+ it.y5);log.error('value5-->'+value5);log.error('VENPOr5-->'+VENPOr5)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 5 de '+value5)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", it.y1 != null ? it.y1 : 0)
                            row.put("y2aux", it.y2 != null ? it.y2 : 0)
                            row.put("y3aux", it.y3 != null ? it.y3 : 0)
                            row.put("y4aux", it.y4 != null ? it.y4 : 0)
                            row.put("y5aux", value5 != null ? value5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP')
                            gh.updateRowGrid(issue.getId(), gridIDPEP, user, row, it.id)
                        }
                    }
					gridData.each() //por cada Linea de Verificacion LC finance
					{	
                        log.error('it.tipopep.name-->'+it.tipopep.name)
                        
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value1< VENPOr1 ){
                            log.warn(' it.y1-->'+ it.y1);log.error('value1-->'+value1);log.error('VENPOr1-->'+VENPOr1)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 1 de '+value1)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", value1 != null ? value1 : 0)
                            row.put("y2aux", it.y2 != null ? it.y2 : 0)
                            row.put("y3aux", it.y3 != null ? it.y3 : 0)
                            row.put("y4aux", it.y4 != null ? it.y4 : 0)
                            row.put("y5aux", it.y5 != null ? it.y5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP del año 1')
                            gh.updateRowGrid(issue.getId(), gridID, user, row, it.id)
                        }
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value2 < VENPOr2 ){
                            log.warn(' it.y2-->'+ it.y2);log.error('value2-->'+value2);log.error('VENPOr2-->'+VENPOr2)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 2 de '+value2)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", it.y1 != null ? it.y1 : 0)
                            row.put("y2aux", value2 != null ? value2: 0)
                            row.put("y3aux", it.y3 != null ? it.y3 : 0)
                            row.put("y4aux", it.y4 != null ? it.y4 : 0)
                            row.put("y5aux", it.y5 != null ? it.y5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP del año 2')
                            gh.updateRowGrid(issue.getId(), gridID, user, row, it.id)
                        }
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value3 < VENPOr3){
                            log.warn(' it.y3-->'+ it.y3);log.error('value3-->'+value3);log.error('VENPOr3-->'+VENPOr3)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 3 de '+value3)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", it.y1 != null ? it.y1 : 0)
                            row.put("y2aux", it.y2 != null ? it.y2 : 0)
                            row.put("y3aux", value3 != null ? value3 : 0)
                            row.put("y4aux", it.y4 != null ? it.y4 : 0)
                            row.put("y5aux", it.y5 != null ? it.y5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP del año 3')
                            gh.updateRowGrid(issue.getId(), gridID, user, row, it.id)
                        }
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value4 < VENPOr4){
                            log.warn(' it.y4-->'+ it.y4);log.error('value4-->'+value4);log.error('VENPOr4-->'+VENPOr4)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 4 de '+value4)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", it.y1 != null ? it.y1 : 0)
                            row.put("y2aux", it.y2 != null ? it.y2 : 0)
                            row.put("y3aux", it.y3 != null ? it.y3 : 0)
                            row.put("y4aux", value4 != null ? value4 : 0)
                            row.put("y5aux", it.y5 != null ? it.y5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP del año 4')
                            gh.updateRowGrid(issue.getId(), gridID, user, row, it.id)
                        }
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep && value5 < VENPOr5){
                            log.warn(' it.y5-->'+ it.y5);log.error('value5-->'+value5);log.error('VENPOr5-->'+VENPOr5)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 5 de '+value5)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", it.y1 != null ? it.y1 : 0)
                            row.put("y2aux", it.y2 != null ? it.y2 : 0)
                            row.put("y3aux", it.y3 != null ? it.y3 : 0)
                            row.put("y4aux", it.y4 != null ? it.y4 : 0)
                            row.put("y5aux", value5 != null ? value5 : 0)
                            log.error('Se modifica el ENP existente en tabla PEP')
                            gh.updateRowGrid(issue.getId(), gridID, user, row, it.id)
                        }
                    }
                }
            }
        }    
    }
}
}






/*
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import groovy.transform.Field
import com.atlassian.jira.ComponentManager;
import Helper.CommonHelper
import Helper.BDHelper
import Helper.GridHelper
import Helper.NfeedHelper
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.index.IssueIndexingService

//Constantes y managers
@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()

/************Acceso issue CONSOLA******************* /
//def issue = ComponentAccessor.getIssueManager().getIssueObject("AJ-1095")
//IDs campos
def gridID = 18221L //  Verificacion LC finance
def gridIDPEP = 18120L //  tabla PEP
def gridIDMod = 18214L // Modificacion PEP existente
//usuario de accion
def user = ch.getAdminUser()
def pluginAccessor = ComponentAccessor.getPluginAccessor();
changeSetClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.dto.TGEChangeSet");
def gridData = gh.getAllGridRows(issue.id, gridID, user)
def gridDataPEP = gh.getAllGridRows(issue.id, gridIDPEP, user)
def gridDataMod = gh.getAllGridRows(issue.id, gridIDMod, user)
double sumaSoly = 0.0
double ValEnp = 0.0
def Id 
def valueS1=0d;def valueS2=0d;def valueS3=0d;def valueS4=0d;def valueS5=0d;
def value1 = 0d;def value2 = 0d;def value3 = 0d;def value4 = 0d;def value5 = 0d
def ID_ENP
def ID_SOF
ch.message('Si se han rebajado los importes de software (PEP de tipo SOF), el importe de los PEP de ENP asociados también se ha recalculado.', 1)
gridDataPEP.each() //por cada Linea de Verificacion LC finance
{
    log.warn(it)
    valueS1 = (it.soly1 == null ? 0.0 : it.soly1)
	valueS2 = (it.soly2 == null ? 0.0 : it.soly2)
	valueS3 = (it.soly3 == null ? 0.0 : it.soly3)
	valueS4 = (it.soly4 == null ? 0.0 : it.soly4)
	valueS5 = (it.soly5 == null ? 0.0 : it.soly5)
	sumaSoly = valueS1+valueS2+valueS3+valueS4+valueS5
    if (it.tipopep.name == 'SOF' && sumaSoly < 0.0){
		log.error('Tipo de PEP = '+it.tipopep.name+' y el solicitado = '+sumaSoly)
        ID_SOF = it.IDpep
        value1 = it.y1.multiply(0.125)
		value2 = it.y2.multiply(0.125)
		value3 = it.y3.multiply(0.125)
		value4 = it.y4.multiply(0.125)
		value5 = it.y5.multiply(0.125)
        //log.error('Id SOF = '+it.IDpep+' y la suma del solicitado es '+sumaSoly)
           gridDataMod.each() //por cada Linea de Modificacion PEP existentes
           {
            //log.error(it)
            if (it.tipopep == 'SOF' && ID_SOF == it.nompep) {
                //log.error('Aporbado del SOF '+it.nompep+' = '+it.totalAprobado)
                ValEnp = it.totalAprobado.multiply(0.125)
                Id = it.nompep
                /*value1 = it.solicitadoY1.multiply(0.125)
                value2 = it.solicitadoY2.multiply(0.125)
                value3 = it.solicitadoY3.multiply(0.125)
                value4 = it.solicitadoY4.multiply(0.125)
                value5 = it.solicitadoY5.multiply(0.125)* /
            gridDataMod.each() //por cada Linea de Modificacion PEP existentes
            {
                if (ValEnp == it.totalAprobado && it.tipopep == 'ENP' && it.tipopep)  {
                    ID_ENP = it.nompep
                    gridDataPEP.each() //por cada Linea de Verificacion LC finance
					{	
                        //log.error('it.tipopep.name-->'+it.tipopep.name)
                        //log.warn('ID_ENP-->'+ID_ENP)
                        //log.error('value1-->'+value1)
                        if(it.tipopep.name == 'ENP' && ID_ENP == it.IDpep){
                            log.error('VER ID-->'+it.id)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 1 de '+value1)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 2 de '+value2)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 3 de '+value3)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 4 de '+value4)
                            log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 5 de '+value5)
                            Map < String, Object > row = new HashMap < String, Object > ();
                            row.put("y1aux", value1 != null ? value1 : 0)
                            row.put("y2aux", value2 != null ? value2 : 0)
                            row.put("y3aux", value3 != null ? value3 : 0)
                            row.put("y4aux", value4 != null ? value4 : 0)
                            row.put("y5aux", value5 != null ? value5 : 0)
                            log.error('Se modifica el ENP existente')
                            gh.updateRowGrid(issue.getId(), gridIDPEP, user, row, it.id)
                        }
                    }
                }
            }
        }    
    }
}
}

*/



