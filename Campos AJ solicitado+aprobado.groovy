import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.ImportUtils
import com.atlassian.plugin.PluginAccessor
import java.text.DecimalFormat

//Guarda la informaci?n de costes aprobados y aprobados + solicitados en html

Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("AJ-891");

//obtiene los dos campos personalizados y crea las variables que se utilizar?n
Long aprobados_id 						= 14500L;	//Coste Aprobado
Long aprobadosSolicitados_id 			= 14501L;	//Coste Aprobado + Solicitado
Long solicitado_id 						= 12317L;	//Coste Solicitado

Long costesExistentes_id 				= 12742L;	//Costes existentes
Long costes_id 							= 15419L;	//Costes Subservicio
Long pep_existente						= 18214L;	//PEP Existente
Long coste_one_time						= 18218L;	//Costes One Time

Long categoria_id						= 11500L;	//Categoría (campo perteneciente a los tickets PPR)
//Long cf_id_year1 = 12667L;
//Long cf_id_year2 = 12668L;
//Long cf_id_year3 = 12670L;
//Long cf_id_year4 = 12669L;
//Long cf_id_year5 = 12671L;
//
//Long cf_id_sol_y1 = 12660L
//Long cf_id_sol_y2 = 12661L
//Long cf_id_sol_y3 = 12662L
//Long cf_id_sol_y4 = 12663L
//Long cf_id_sol_y5 = 12664L

CustomFieldManager customFieldManager 	= ComponentAccessor.getCustomFieldManager();
CustomField aprobados_cf 				= customFieldManager.getCustomFieldObject(aprobados_id);
CustomField aprobadosSolicitados_cf 	= customFieldManager.getCustomFieldObject(aprobadosSolicitados_id);
CustomField solicitado_cf 				= customFieldManager.getCustomFieldObject(solicitado_id)
CustomField costesExistentes_cf 		= customFieldManager.getCustomFieldObject(costesExistentes_id);
CustomField categoria_cf				= customFieldManager.getCustomFieldObject(categoria_id);
//CustomField cf_year1 					= customFieldManager.getCustomFieldObject(cf_id_year1);
//CustomField cf_year2					= customFieldManager.getCustomFieldObject(cf_id_year2);
//CustomField cf_year3  					= customFieldManager.getCustomFieldObject(cf_id_year3);
//CustomField cf_year4					= customFieldManager.getCustomFieldObject(cf_id_year4);
//CustomField cf_year5					= customFieldManager.getCustomFieldObject(cf_id_year5);
//CustomField cf_sol_year1 				= customFieldManager.getCustomFieldObject(cf_id_sol_y1);
//CustomField cf_sol_year2				= customFieldManager.getCustomFieldObject(cf_id_sol_y2);
//CustomField cf_sol_year3  				= customFieldManager.getCustomFieldObject(cf_id_sol_y3);
//CustomField cf_sol_year4				= customFieldManager.getCustomFieldObject(cf_id_sol_y4);
//CustomField cf_sol_year5				= customFieldManager.getCustomFieldObject(cf_id_sol_y5);

totalAprobado 							= [0.0D, 0.0D, 0.0D, 0.0D, 0.0D].toArray();
totalAprobado2							= [0.0D, 0.0D, 0.0D, 0.0D, 0.0D].toArray();
totalAprobadoSolicitado 				= [0.0D, 0.0D, 0.0D, 0.0D, 0.0D].toArray();
totalSolicitado 						= [0.0D, 0.0D, 0.0D, 0.0D, 0.0D].toArray();
totalSolicitado2						= [0.0D, 0.0D, 0.0D, 0.0D, 0.0D].toArray();
String aprobadosHtml
DecimalFormat df 						= new DecimalFormat("#.0");

//Contadores para saber si en las tablas hay valores diferentes de 0
contApr = [0.0D].toArray()
contAprSol = [0.0D].toArray()
contSol = [0.0D].toArray()
contSol2 = [0.0D].toArray()

//crea la cabecera de los campos html
    aprobadosHtml = """
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Tipo de PEP</th>
               	 <th>Nombre PEP</th>
                 <th>T. Aprobado</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
        </tr>
             
             """

String solicitadosHtml2 = """
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Entrega</th>
               	 <th>Descripción</th>
                 <th>Unidad</th>
                 <th>Grupo de Artículos</th>
                 <th>Subservicio</th>
                 <th>Proveedor</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
        </tr>
             
             """


String aprobadosSolicitadosHtml = """
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Tipo de PEP</th>
               	 <th>Nombre PEP</th>
                 <th>T. Solicitado</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
             </tr>
             
             """
	

solicitadosHtml = """
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Tipo de PEP</th>
               	 <th>Nombre PEP</th>
                 <th>T. Solicitado</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
             </tr>
             
             """


//obtiene las filas del campo costes existentes e itera seteando los valores de la tabla.
Object userObject 				= ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
User user 						= userObject instanceof ApplicationUser ? ((ApplicationUser) userObject).getDirectoryUser() : (User) userObject
PluginAccessor pluginAccessor 	= ComponentAccessor.getPluginAccessor();
//Class tgeConfigManagerClass 	= pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.config.grid.TGEGridConfigManager");
//def tgeConfigManager 			= ComponentAccessor.getOSGiComponentInstanceOfType(tgeConfigManagerClass);
Class dataManagerClass 			= pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
def tgeGridDataManager 			= ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
//StringBuilder result 			= new StringBuilder();
//return isChangeRequest(issue.getCustomFieldValue(categoria_cf))
try {
    //if (categoria == "Change request (Ampliación)"){
    //if (categoria?.optionId == 10903L){
        def call = tgeGridDataManager.readGridData(issue.getId(), pep_existente, null, null, 0, tgeGridDataManager.getRowCount(issue.getId(),pep_existente,user), user);
        for(HashMap row: call.getValues()){
            log.error("Row1: "+row);
                aprobadosHtml 				+= setApproved(row);
				aprobadosSolicitadosHtml 	+= setApprovedAndRequested(row)
            
            
            solicitadosHtml 			+= setSolicitado(row)
            //log.error(row)
        };
        def call2 = tgeGridDataManager.readGridData(issue.getId(), coste_one_time, null, null, 0, tgeGridDataManager.getRowCount(issue.getId(),coste_one_time,user), user);
        for(HashMap row: call2.getValues()){
            log.error("0Row2--> "+row);
            log.error("0ROW y5-->"+row.y5)
            log.error('0ROW y1'+row.y1)
            solicitadosHtml2 				+= setSolicitado2(row);
            //log.error('1 - solicitadosHtml2-->'+solicitadosHtml2)
        };
    
}catch(Exception e){
    log.error(e.getMessage());
    log.error("No se puedo encontrar el valor del campo.");
}

//cierra el html de los campos.
    aprobadosHtml 				+= setEndOfTable(totalAprobado, 2);
	aprobadosSolicitadosHtml 	+= setEndOfTables(totalAprobadoSolicitado, 3);

//Si es 0 no se pone
    solicitadosHtml 			+= setEndOfTable(totalSolicitado, 2);
//MFS - INI
solicitadosHtml2 			+= setEndOfTable(totalAprobado2, 5);
//MFS - FIN
//log.error(aprobadosHtml);
//log.error(aprobadosSolicitadosHtml);



//MFS - INI
//setea los valores de los campos html y reindexa el issue
    aprobados_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(aprobados_cf),"{html}"+aprobadosHtml+"{html}"),new DefaultIssueChangeHolder());
if (contAprSol[0] > 0 && contSol2[0] > 0){
	aprobadosSolicitados_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(aprobadosSolicitados_cf),"{html}"+aprobadosSolicitadosHtml+solicitadosHtml2+"{html}"),new DefaultIssueChangeHolder());
	log.error('1.-Entra aqui')
}
if (contAprSol[0] > 0 && contSol2[0] == 0){
	aprobadosSolicitados_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(aprobadosSolicitados_cf),"{html}"+aprobadosSolicitadosHtml+"{html}"),new DefaultIssueChangeHolder());
	log.error('2.-Entra aqui')
}
//Si todas son !=0 imprime las dos tablas
if (contSol[0] > 0 && contSol2[0] > 0){
    solicitado_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(solicitado_cf),"{html}"+solicitadosHtml+ solicitadosHtml2+"{html}"),new DefaultIssueChangeHolder());
}
//Si una si que tiene el total 0 i la otra no, solo imprime una
if (contSol[0] > 0 && contSol2[0] == 0){
    solicitado_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(solicitado_cf),"{html}"+solicitadosHtml+"{html}"),new DefaultIssueChangeHolder());
}
if (contSol[0] == 0 && contSol2[0] > 0){
    solicitado_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(solicitado_cf),"{html}"+solicitadosHtml2+"{html}"),new DefaultIssueChangeHolder());
}
//Si las dos son 0 no se pone ninguna
if (contSol[0] == 0 && contSol2[0] == 0){
    solicitado_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(solicitado_cf),null),new DefaultIssueChangeHolder());
}

//MFS - FIN
//Se le dan valores a los campos numéricos del propio issue  PS
//cf_year1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year1), totalAprobado[0]),new DefaultIssueChangeHolder());
//cf_year2.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year2), totalAprobado[1]),new DefaultIssueChangeHolder());
//cf_year3.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year3), totalAprobado[2]),new DefaultIssueChangeHolder());
//cf_year4.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year4), totalAprobado[3]),new DefaultIssueChangeHolder());
//cf_year5.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year5), totalAprobado[4]),new DefaultIssueChangeHolder());

//cf_sol_year1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year1), totalSolicitado[0]),new DefaultIssueChangeHolder());
//cf_sol_year2.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year2), totalSolicitado[1]),new DefaultIssueChangeHolder());
//cf_sol_year3.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year3), totalSolicitado[2]),new DefaultIssueChangeHolder());
//cf_sol_year4.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year4), totalSolicitado[3]),new DefaultIssueChangeHolder());
//cf_sol_year5.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year5), totalSolicitado[4]),new DefaultIssueChangeHolder());


//se guardan los cambios y reindexa issue 
issue.store();
reindexar(issue);

//metodos

//setea una fila del campo costes aprobados
//if (isChangeRequest(issue.getCustomFieldValue(categoria_cf))){
String setApproved(HashMap row){
    totalAprobado[0] += row.get("aprobadoY1");
    totalAprobado[1] += row.get("aprobadoY2");
    totalAprobado[2] += row.get("aprobadoY3");
    totalAprobado[3] += row.get("aprobadoY4");
    totalAprobado[4] += row.get("aprobadoY5");
    
    //Se valida si hay valores != 0
   if(row.get("aprobadoY1") != 0 || row.get("aprobadoY2") != 0 || row.get("aprobadoY3") != 0 || row.get("aprobadoY4") != 0 || row.get("aprobadoY5") != 0) {
  		contApr[0] = contApr[0]+1
    }

    log.error("Tipo Plan: ${row.get("tipoPlan")}")
    return """<tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
					 <td>${row.get("tipopep")}</td>           
                     <td>${row.get("nompep")}</td>
                     <td>${row.get("totalAprobado")}</td>
                     <td>${row.get("aprobadoY1").round(2)}</td>
                     <td>${row.get("aprobadoY2").round(2)}</td>
                     <td>${row.get("aprobadoY3").round(2)}</td>
                     <td>${row.get("aprobadoY4").round(2)}</td>
                     <td>${row.get("aprobadoY5").round(2)}</td>
                    </tr>"""
}
//}
String setSolicitado2(HashMap row){

    totalAprobado2[0] += row.get("y1");
    totalAprobado2[1] += row.get("y2");
    totalAprobado2[2] += row.get("y3");
    totalAprobado2[3] += row.get("y4");
    totalAprobado2[4] += row.get("y5");
    
    //Se valida si hay valores != 0
   if(row.get("y1") != 0 || row.get("y2") != 0 || row.get("y3") != 0 || row.get("y4") != 0 || row.get("y5") != 0) {
  		contSol2[0] = contSol2[0]+1
    }
    
    log.error("####Tipo Plan: ${row.get("tipoPlan")}")
    log.error("#### ROW: ${row.y1.class}")
    log.error("#### ROW: ${row.y3 > 10000000 ?   String.format("%04f",row.get("y3")) : row.y3.round(2)         }")
    //MFS - INI
    return """<tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
					 <td>${row.get("actividad").get("name")}</td>           
                     <td>${row.get("descripcion")}</td>        
					 <td>${row.unidadit.name}</td>
					 <td>${row.grupoart.name}</td>
					 <td>${row.subservventa.name}</td>
					 <td>${row.proveedor.name}</td>
                     <td>${row.y1 > 10000000 ?   String.format("%04f",row.get("y1")) : row.y1.round(2)         }</td>
                     <td>${row.y2 > 10000000 ?   String.format("%04f",row.get("y2")) : row.y2.round(2)         }</td>
                     <td>${row.y3 > 10000000 ?   String.format("%04f",row.get("y3")) : row.y3.round(2)         }</td>
                     <td>${row.y4 > 10000000 ?   String.format("%04f",row.get("y4")) : row.y4.round(2)         }</td>
                     <td>${row.y5 > 10000000 ?   String.format("%04f",row.get("y5")) : row.y5.round(2)         }</td>
                    </tr>"""
    //MFS - FIN
    // log.warn("row.get('year1'):  ${row.get("year1").class}  ")
    // String.format("%06d",row.get("year1"))
}


//setea una fila del campo costes aprobados + solicitados (Costes existentes)
String setApprovedAndRequested(HashMap row){
    totalAprobadoSolicitado[0] += row.get("solicitadoY1") + row.get("aprobadoY1");
    totalAprobadoSolicitado[1] += row.get("solicitadoY2") + row.get("aprobadoY2");
    totalAprobadoSolicitado[2] += row.get("solicitadoY3") + row.get("aprobadoY3");
    totalAprobadoSolicitado[3] += row.get("solicitadoY4") + row.get("aprobadoY4");
    totalAprobadoSolicitado[4] += row.get("solicitadoY5") + row.get("aprobadoY5")
   
    //Se valida si hay valores != 0
   if(totalAprobadoSolicitado[0] != 0 || totalAprobadoSolicitado[1] != 0 || totalAprobadoSolicitado[2] != 0 || totalAprobadoSolicitado[3] != 0 || totalAprobadoSolicitado[4] != 0) {
  		contAprSol[0] = contAprSol[0]+1
    }
    
    log.error("Tipo Plan: ${row.get("tipoPlan")}")
log.error('totales-------------------->')
    return """<tr>
					<td>${row.get("tipopep") == null ? "---" : row.get("tipopep")}</td>
					<td>${row.get("nompep")}</td>
                    <td>${row.get("totalAprobado") + row.get("totalSolicitado")}</td>
                    <td>${(row.get("solicitadoY1")+row.get("aprobadoY1")).round(2)}</td>
					<td>${(row.get("solicitadoY2")+row.get("aprobadoY2")).round(2)}</td>
                    <td>${(row.get("solicitadoY3")+row.get("aprobadoY3")).round(2)}</td>
					<td>${(row.get("solicitadoY4")+row.get("aprobadoY4")).round(2)}</td>
                    <td>${(row.get("solicitadoY5")+row.get("aprobadoY5")).round(2)}</td>
					<td>${row.get("tipoPlan") == "G" ? "Si" : row.get("tipoPlan") == "GNPD" ? "No" : " "}</td>
			</tr> 
            
            """

}

String setSolicitado(HashMap row){


    totalSolicitado[0] += row.get("solicitadoY1")
    totalSolicitado[1] += row.get("solicitadoY2")
    totalSolicitado[2] += row.get("solicitadoY3")
    totalSolicitado[3] += row.get("solicitadoY4")
    totalSolicitado[4] += row.get("solicitadoY5")
   
    //Se valida si hay valores != 0
   if(row.get("solicitadoY1") != 0 || row.get("solicitadoY2") != 0 || row.get("solicitadoY3") != 0 || row.get("solicitadoY4") != 0 || row.get("solicitadoY5") != 0) {
  		contSol[0] = contSol[0] + 1
    }
    log.error("hay valores en el sol 2? "+contSol2)

    
log.warn('TOTAL SOLICITADO='+row.get("totalSolicitado"))
log.error('TOTAL DOLICITADO 2=>'+(totalSolicitado[0]+totalSolicitado[1]+totalSolicitado[2]+totalSolicitado[3]+totalSolicitado[4]))
    return """<tr>
					<td>${row.get("tipopep")}</td>
                    <td>${row.get("nompep")}</td>
                    <td>${row.get("totalSolicitado")}</td>
                    <td>${row.get("solicitadoY1").round(2)}</td>
					<td>${row.get("solicitadoY2").round(2)}</td>
                    <td>${row.get("solicitadoY3").round(2)}</td>
					<td>${row.get("solicitadoY4").round(2)}</td>
                    <td>${row.get("solicitadoY5").round(2)}</td>
			  </tr> 
            """

}

//devuelve el final de la tabla
String setEndOfTable(def totalCosts, def spaceNumber){
    def endText = """<trstyle='border-bottom: 1px solid #ccc; line-height: 1.8em;'>"""

    for (int i = 0; i < spaceNumber-1; i++) {
        endText += """<td></td>\n"""
    }
    //MFS - INI
		log.warn('0 Totales----->'+totalCosts[0]+totalCosts[1]+totalCosts[2]+totalCosts[3]+totalCosts[4])

    endText += """<td>TOTAL:</td>
                     <td>${totalCosts[0]+totalCosts[1]+totalCosts[2]+totalCosts[3]+totalCosts[4]}</td>
                     <td>${totalCosts[0]> 10000000 ?   String.format("%04f",totalCosts[0]) :totalCosts[0].round(2)    }</td>
                     <td>${totalCosts[1]> 10000000 ?   String.format("%04f",totalCosts[1]) :totalCosts[1].round(2)    }</td>
                     <td>${totalCosts[2]> 10000000 ?   String.format("%04f",totalCosts[2]) :totalCosts[2].round(2)    }</td>
                     <td>${totalCosts[3]> 10000000 ?   String.format("%04f",totalCosts[3]) :totalCosts[3].round(2)    }</td>
                     <td>${totalCosts[4]> 10000000 ?   String.format("%04f",totalCosts[4]) :totalCosts[4].round(2)    }</td>
              		 <td></td>
                </tr>
            </tbody>
            </table>
            """
    //MFS - FIN
    return endText
}
String setEndOfTables(def totalCosts, def spaceNumber){
    def endText = """<trstyle='border-bottom: 1px solid #ccc; line-height: 1.8em;'>"""

    for (int i = 0; i < spaceNumber-2; i++) {
        endText += """<td></td>\n"""
    }
	log.warn('Totales----->'+totalCosts[0]+totalCosts[1]+totalCosts[2]+totalCosts[3]+totalCosts[4])
    //MFS - INI
    endText += """<td>TOTAL:</td>
                     <td>${totalCosts[0]+totalCosts[1]+totalCosts[2]+totalCosts[3]+totalCosts[4]}</td>
                     <td>${totalCosts[0]> 10000000 ?   String.format("%04f",totalCosts[0]) :totalCosts[0].round(2)    }</td>
                     <td>${totalCosts[1]> 10000000 ?   String.format("%04f",totalCosts[1]) :totalCosts[1].round(2)    }</td>
                     <td>${totalCosts[2]> 10000000 ?   String.format("%04f",totalCosts[2]) :totalCosts[2].round(2)    }</td>
                     <td>${totalCosts[3]> 10000000 ?   String.format("%04f",totalCosts[3]) :totalCosts[3].round(2)    }</td>
                     <td>${totalCosts[4]> 10000000 ?   String.format("%04f",totalCosts[4]) :totalCosts[4].round(2)    }</td>
              		 <td></td>
                </tr>
            </tbody>
            </table>
            """
    //MFS - FIN
    return endText
}


//it reindexes issues
def reindexar (def issue_r){
    try{
        def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
        boolean wasIndexing = ImportUtils.isIndexIssues();

        ImportUtils.setIndexIssues(true);
        issueIndexingService.reIndex(issue_r)
        ImportUtils.setIndexIssues(wasIndexing)

        return true
    }
    catch (Exception e){

        return false
    }
}

    /*aprobadosHtml 				
	aprobadosSolicitadosHtml 	
    solicitadosHtml
    solicitadosHtml2
    
    aprobadosSolicitados_cf
    aprobados_cf
    solicitado_cf*/	
return issue.getCustomFieldValue(aprobadosSolicitados_cf)


/*import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.ImportUtils
import com.atlassian.plugin.PluginAccessor
import java.text.DecimalFormat

//Guarda la informaci?n de costes aprobados y aprobados + solicitados en html

//PRUEBAS
//Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("AJ-360");

//obtiene los dos campos personalizados y crea las variables que se utilizar?n
Long aprobados_id 						= 14500L;	//Coste Aprobado
Long aprobadosSolicitados_id 			= 14501L;	//Coste Aprobado + Solicitado
Long solicitado_id 						= 12317L
Long costesExistentes_id 				= 12742L;	//Costes existentes
Long costes_id 							= 15419L;	//Costes Subservicio
Long pep_existente						= 18214L;	//Ajustes PEP Existente
Long coste_one_time						= 17103L;	//Costes One Time Ajustes
Long ajustes_pep_existente				= 18218L;	//Alta PEP
Long cf_id_year1 = 12667L;
Long cf_id_year2 = 12668L;
Long cf_id_year3 = 12670L;
Long cf_id_year4 = 12669L;
Long cf_id_year5 = 12671L;

Long cf_id_sol_y1 = 12660L
Long cf_id_sol_y2 = 12661L
Long cf_id_sol_y3 = 12662L
Long cf_id_sol_y4 = 12663L
Long cf_id_sol_y5 = 12664L

CustomFieldManager customFieldManager 	= ComponentAccessor.getCustomFieldManager();
CustomField aprobados_cf 				= customFieldManager.getCustomFieldObject(aprobados_id);
CustomField aprobadosSolicitados_cf 	= customFieldManager.getCustomFieldObject(aprobadosSolicitados_id);
CustomField solicitado_cf 				= customFieldManager.getCustomFieldObject(solicitado_id)
CustomField costesExistentes_cf 		= customFieldManager.getCustomFieldObject(costesExistentes_id);
CustomField cf_year1 					= customFieldManager.getCustomFieldObject(cf_id_year1);
CustomField cf_year2					= customFieldManager.getCustomFieldObject(cf_id_year2);
CustomField cf_year3  					= customFieldManager.getCustomFieldObject(cf_id_year3);
CustomField cf_year4					= customFieldManager.getCustomFieldObject(cf_id_year4);
CustomField cf_year5					= customFieldManager.getCustomFieldObject(cf_id_year5);
CustomField cf_sol_year1 				= customFieldManager.getCustomFieldObject(cf_id_sol_y1);
CustomField cf_sol_year2				= customFieldManager.getCustomFieldObject(cf_id_sol_y2);
CustomField cf_sol_year3  				= customFieldManager.getCustomFieldObject(cf_id_sol_y3);
CustomField cf_sol_year4				= customFieldManager.getCustomFieldObject(cf_id_sol_y4);
CustomField cf_sol_year5				= customFieldManager.getCustomFieldObject(cf_id_sol_y5);

totalAprobado 							= [0.0D, 0.0D, 0.0D, 0.0D, 0.0D].toArray();
totalAprobadoSolicitado 				= [0.0D, 0.0D, 0.0D, 0.0D, 0.0D].toArray();
totalSolicitado 						= [0.0D, 0.0D, 0.0D, 0.0D, 0.0D].toArray();
DecimalFormat df 						= new DecimalFormat("#.0");

//crea la cabecera de los campos html

String aprobadosHtml = """{html}
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Tipo de PEP</th>
               	 <th>Nombre PEP</th>
                 <th>T. Aprobado</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
        </tr>
             
             """

String aprobadosSolicitadosHtml = """{html}
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Tipo de PEP</th>
               	 <th>Nombre PEP</th>
                 <th>T. Aprobado</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
             </tr>
             
             """
			 
String solicitadosHtml = """{html}
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Tipo de PEP</th>
               	 <th>Nombre PEP</th>
                 <th>T. Solicitado</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
             </tr>
             
             """

//obtiene las filas del campo costes existentes e itera seteando los valores de la tabla.
Object userObject 				= ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
User user 						= userObject instanceof ApplicationUser ? ((ApplicationUser) userObject).getDirectoryUser() : (User) userObject
PluginAccessor pluginAccessor 	= ComponentAccessor.getPluginAccessor();
//Class tgeConfigManagerClass 	= pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.config.grid.TGEGridConfigManager");
//def tgeConfigManager 			= ComponentAccessor.getOSGiComponentInstanceOfType(tgeConfigManagerClass);
Class dataManagerClass 			= pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
def tgeGridDataManager 			= ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
//StringBuilder result 			= new StringBuilder();

try {
    def call = tgeGridDataManager.readGridData(issue.getId(), pep_existente, null, null, 0, tgeGridDataManager.getRowCount(issue.getId(),pep_existente,user), user);
    for(HashMap row: call.getValues()){
		log.error("Row: "+row);
        aprobadosHtml 				+= setApproved(row);
        aprobadosSolicitadosHtml 	+= setApprovedAndRequested(row);
		solicitadosHtml 			+= setSolicitado(row)
        //log.error(row)
    };
    def call2 = tgeGridDataManager.readGridData(issue.getId(), ajustes_pep_existente, null, null, 0, tgeGridDataManager.getRowCount(issue.getId(),ajustes_pep_existente,user), user);
    for(HashMap row: call2.getValues()){
		log.error("Row: "+row);
        solicitadosHtml 			+= setSolicitado2(row)
        aprobadosSolicitadosHtml 	+= setApprovedAndRequested2(row);
    }
    //log.error(aprobadosHtml);

}catch(Exception e){
    log.error(e.getMessage());
    log.error("No se puedo encontrar el valor del campo.");
}

//cierra el html de los campos.

aprobadosHtml 				+= setEndOfTable(totalAprobado, 1);
aprobadosSolicitadosHtml 	+= setEndOfTable(totalAprobadoSolicitado, 1);
solicitadosHtml 			+= setEndOfTable(totalSolicitado, 1);

//log.error(aprobadosHtml);
//log.error(aprobadosSolicitadosHtml);


//setea los valores de los campos html y reindexa el issue

aprobados_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(aprobados_cf),aprobadosHtml),new DefaultIssueChangeHolder());
aprobadosSolicitados_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(aprobadosSolicitados_cf),aprobadosSolicitadosHtml),new DefaultIssueChangeHolder());
solicitado_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(solicitado_cf), solicitadosHtml),new DefaultIssueChangeHolder());

//Se le dan valores a los campos numéricos del propio issue  PS
cf_year1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year1), totalAprobado[0]),new DefaultIssueChangeHolder());
cf_year2.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year2), totalAprobado[1]),new DefaultIssueChangeHolder());
cf_year3.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year3), totalAprobado[2]),new DefaultIssueChangeHolder());
cf_year4.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year4), totalAprobado[3]),new DefaultIssueChangeHolder());
cf_year5.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_year5), totalAprobado[4]),new DefaultIssueChangeHolder());

cf_sol_year1.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year1), totalSolicitado[0]),new DefaultIssueChangeHolder());
cf_sol_year2.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year2), totalSolicitado[1]),new DefaultIssueChangeHolder());
cf_sol_year3.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year3), totalSolicitado[2]),new DefaultIssueChangeHolder());
cf_sol_year4.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year4), totalSolicitado[3]),new DefaultIssueChangeHolder());
cf_sol_year5.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf_sol_year5), totalSolicitado[4]),new DefaultIssueChangeHolder());


//se guardan los cambios y reindexa issue 
issue.store();
reindexar(issue);

//metodos

//setea una fila del campo costes aprobados
String setApproved(HashMap row){

    totalAprobado[0] += row.get("aprobadoY1");
    totalAprobado[1] += row.get("aprobadoY2");
    totalAprobado[2] += row.get("aprobadoY3");
    totalAprobado[3] += row.get("aprobadoY4");
    totalAprobado[4] += row.get("aprobadoY5");
    
    def nombrePep = (row.get("nompep") == null) ? "" : row.get("nompep")
//MFS-INI
	log.error("Tipo Plan: ${row.get("tipoPlan")}")
    return """<tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
					 <td>${row.get("tipopep")}</td>           
                     <td>${nombrePep}</td>
                     <td>${row.get("totalAprobado")}</td>
                     <td>${row.get("aprobadoY1").round(2)}</td>
                     <td>${row.get("aprobadoY2").round(2)}</td>
                     <td>${row.get("aprobadoY3").round(2)}</td>
                     <td>${row.get("aprobadoY4").round(2)}</td>
                     <td>${row.get("aprobadoY5").round(2)}</td>
                    </tr>"""
//MFS - FIN
}

//setea una fila del campo costes aprobados + solicitados (Costes existentes)
String setApprovedAndRequested(HashMap row){

    totalAprobadoSolicitado[0] += row.get("solicitadoY1") + row.get("aprobadoY1");
    totalAprobadoSolicitado[1] += row.get("solicitadoY2") + row.get("aprobadoY2");
    totalAprobadoSolicitado[2] += row.get("solicitadoY3") + row.get("aprobadoY3");
    totalAprobadoSolicitado[3] += row.get("solicitadoY4") + row.get("aprobadoY4");
    totalAprobadoSolicitado[4] += row.get("solicitadoY5") + row.get("aprobadoY5")
	log.error("Tipo Plan: ${row.get("tipoPlan")}")
    
    def nombrePep = (row.get("nompep") == null) ? "" : row.get("nompep")
//MFS - INI
    return """<tr>
					<td>${row.get("tipopep")}</td>           
                    <td>${nombrePep}</td>
                    <td>${(row.get("totalSolicitado")+row.get("totalAprobado")).round(2)}</td>
                    <td>${(row.get("solicitadoY1")+row.get("aprobadoY1")).round(2)}</td>
					<td>${(row.get("solicitadoY2")+row.get("aprobadoY2")).round(2)}</td>
                    <td>${(row.get("solicitadoY3")+row.get("aprobadoY3")).round(2)}</td>
					<td>${(row.get("solicitadoY4")+row.get("aprobadoY4")).round(2)}</td>
                    <td>${(row.get("solicitadoY5")+row.get("aprobadoY5")).round(2)}</td>
                                                       </tr> 
            
            """
//MFS - FIN
}

String setApprovedAndRequested2(HashMap row){

    totalAprobadoSolicitado[0] += row.get("y1")
    totalAprobadoSolicitado[1] += row.get("y2")
    totalAprobadoSolicitado[2] += row.get("y3")
    totalAprobadoSolicitado[3] += row.get("y4")
    totalAprobadoSolicitado[4] += row.get("y5")
	
    def nombrePep = (row.get("nompep") == null) ? "" : row.get("nompep")

    return """<tr>
					<td>${row.get("tipopep").get("value")}</td>
                    <td>${nombrePep}</td>
                    <td>${row.get("y1")+row.get("y2")+row.get("y3")+row.get("y4")+row.get("y5")}</td>
                    <td>${row.get("y1").round(2)}</td>
					<td>${row.get("y2").round(2)}</td>
                    <td>${row.get("y3").round(2)}</td>
					<td>${row.get("y4").round(2)}</td>
                    <td>${row.get("y5").round(2)}</td>
			  </tr> 
            """

}

String setSolicitado(HashMap row){

    totalSolicitado[0] += row.get("solicitadoY1")
    totalSolicitado[1] += row.get("solicitadoY2")
    totalSolicitado[2] += row.get("solicitadoY3")
    totalSolicitado[3] += row.get("solicitadoY4")
    totalSolicitado[4] += row.get("solicitadoY5")
    
    def nombrePep = (row.get("nompep") == null) ? "" : row.get("nompep")
//MFS - INI
    return """<tr>
					<td>${row.get("tipopep")}</td>
                    <td>${nombrePep}</td>
                    <td>${row.get("totalSolicitado")}</td>
                    <td>${row.get("solicitadoY1").round(2)}</td>
					<td>${row.get("solicitadoY2").round(2)}</td>
                    <td>${row.get("solicitadoY3").round(2)}</td>
					<td>${row.get("solicitadoY4").round(2)}</td>
                    <td>${row.get("solicitadoY5").round(2)}</td>
			  </tr> 
            """
//MFS - FIN
}

String setSolicitado2(HashMap row){

    totalSolicitado[0] += row.get("y1")
    totalSolicitado[1] += row.get("y2")
    totalSolicitado[2] += row.get("y3")
    totalSolicitado[3] += row.get("y4")
    totalSolicitado[4] += row.get("y5")
    
    def nombrePep = (row.get("nompep") == null) ? "" : row.get("nompep")
//MFS - INI
    return """<tr>
					<td>${row.get("tipopep").get("value")}</td>
                    <td>${nombrePep}</td>
                    <td>${row.get("y1")+row.get("y2")+row.get("y3")+row.get("y4")+row.get("y5")}</td>
                    <td>${row.get("y1").round(2)}</td>
					<td>${row.get("y2").round(2)}</td>
                    <td>${row.get("y3").round(2)}</td>
					<td>${row.get("y4").round(2)}</td>
                    <td>${row.get("y5").round(2)}</td>
			  </tr> 
            """
//MFS - FIN
}

//devuelve el final de la tabla
String setEndOfTable(def totalCosts, def spaceNumber){
    def endText = """<trstyle='border-bottom: 1px solid #ccc; line-height: 1.8em;'>"""

    for (int i = 0; i < spaceNumber; i++) {
        endText += """<td></td>\n"""
    }
    def tot = totalCosts[0]+totalCosts[1]+totalCosts[2]+totalCosts[3]+totalCosts[4]
    endText += """<td>TOTAL:</td>
				     <td>${tot.round(2)}</td>
                     <td>${totalCosts[0].round(2)}</td>
                     <td>${totalCosts[1].round(2)}</td>
                     <td>${totalCosts[2].round(2)}</td>
                     <td>${totalCosts[3].round(2)}</td>
                     <td>${totalCosts[4].round(2)}</td>
              		 <td></td>
                </tr>
            </tbody>
            </table>
            {html}"""
    return endText
}



//it reindexes issues
def reindexar (def issue_r){
    try{
        def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
        boolean wasIndexing = ImportUtils.isIndexIssues();

        ImportUtils.setIndexIssues(true);
        issueIndexingService.reIndex(issue_r)
        ImportUtils.setIndexIssues(wasIndexing)

        return true
    }
    catch (Exception e){

        return false
    }
}*/