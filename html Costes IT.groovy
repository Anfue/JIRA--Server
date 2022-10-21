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

//Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("PPR-8689");

//obtiene los dos campos personalizados y crea las variables que se utilizar?n
Long aprobados_id 						= 14500L;	//Coste Aprobado
Long aprobadosSolicitados_id 			= 14501L;	//Coste Aprobado + Solicitado
Long solicitado_id 						= 12317L;	//Coste Solicitado

Long costesExistentes_id 				= 12742L;	//Costes existentes
Long costes_id 							= 15419L;	//Costes Subservicio
Long pep_existente						= 18214L;	//PEP Existente
Long coste_one_time						= 18116L;	//Costes One Time

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

//crea la cabecera de los campos html

if (!isChangeRequest(issue.getCustomFieldValue(categoria_cf))){

String solicitadosHtml2 = """{html}
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
				 <th>Planned</th>
        </tr>
             
             """


/*String aprobadosSolicitadosHtml = """{html}
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Código Clarity</th>
               	 <th>Actividad</th>
                 <th>Naturaleza</th>
                 <th>Cliente</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
				 <th>Planned</th>
             </tr>
             
             """
	*/		 
String solicitadosHtml = " "
if(totalSolicitado[0] != 0){
solicitadosHtml = """{html}
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
}

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
    def call2 = tgeGridDataManager.readGridData(issue.getId(), coste_one_time, null, null, 0, tgeGridDataManager.getRowCount(issue.getId(),coste_one_time,user), user);
    	for(HashMap row: call2.getValues()){
		log.error("Row2--> "+row);
        log.error("ROW year5-->"+row.year5)
        log.error('ROW year1'+row.year1)
        solicitadosHtml2 				+= setSolicitado2(row);
        log.error('2 - solicitadosHtml2-->'+solicitadosHtml2)
    	};
        //log.error(aprobadosHtml);
    
}catch(Exception e){
    log.error(e.getMessage());
    log.error("No se puedo encontrar el valor del campo.");
}

//cierra el html de los campos.
//aprobadosSolicitadosHtml 	+= setEndOfTable(totalAprobadoSolicitado, 3);
//Si es 0 no se pone
if(totalSolicitado[0] != 0){
solicitadosHtml 			+= setEndOfTable(totalSolicitado, 2);
}
solicitadosHtml2 			+= setEndOfTable(totalAprobado2, 5);
//log.error(aprobadosHtml);
//log.error(aprobadosSolicitadosHtml);


//setea los valores de los campos html y reindexa el issue
//aprobadosSolicitados_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(aprobadosSolicitados_cf),aprobadosSolicitadosHtml),new DefaultIssueChangeHolder());
solicitado_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(solicitado_cf), solicitadosHtml+solicitadosHtml2),new DefaultIssueChangeHolder());

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

    totalAprobado2[0] += row.get("year1");
    totalAprobado2[1] += row.get("year2");
    totalAprobado2[2] += row.get("year3");
    totalAprobado2[3] += row.get("year4");
    totalAprobado2[4] += row.get("year5");

	log.error("Tipo Plan: ${row.get("tipoPlan")}")
    return """<tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
					 <td>${row.get("actividad").get("name")}</td>           
                     <td>${row.get("descripcion")}</td>
                     <td>${row.get("unidadit").get("name")}</td>
                     <td>${row.get("grupoart").get("name")}</td>
                     <td>${row.get("subservventa").get("name")}</td>
                     <td>${row.get("proveedor").get("name")}</td>
                     <td>${row.get("year1").round(2)}</td>
                     <td>${row.get("year2").round(2)}</td>
                     <td>${row.get("year3").round(2)}</td>
                     <td>${row.get("year4").round(2)}</td>
                     <td>${row.get("year5").round(2)}</td>
					 <td>${row.get("planned").get("name")}</td>
                    </tr>"""
}


//setea una fila del campo costes aprobados + solicitados (Costes existentes)
String setApprovedAndRequested(HashMap row){

    totalAprobadoSolicitado[0] += row.get("solicitadoY1") + row.get("aprobadoY1");
    totalAprobadoSolicitado[1] += row.get("solicitadoY2") + row.get("aprobadoY2");
    totalAprobadoSolicitado[2] += row.get("solicitadoY3") + row.get("aprobadoY3");
    totalAprobadoSolicitado[3] += row.get("solicitadoY4") + row.get("aprobadoY4");
    totalAprobadoSolicitado[4] += row.get("solicitadoY5") + row.get("aprobadoY5")
	log.error("Tipo Plan: ${row.get("tipoPlan")}")

    return """<tr>
					<td>${row.get("clarityID")}</td>
                    <td>${row.get("actividad")}</td>
                    <td>${row.get("naturaleza")}</td>
                    <td>${row.get("cliente")}</td>
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

    for (int i = 0; i < spaceNumber; i++) {
        endText += """<td></td>\n"""
    }
    endText += """<td>TOTAL:</td>
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
}


}else {

//crea la cabecera de los campos html
if (isChangeRequest(issue.getCustomFieldValue(categoria_cf))){
aprobadosHtml = """{html}
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
}
String solicitadosHtml2 = """{html}
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
				 <th>Planned</th>
        </tr>
             
             """


/*String aprobadosSolicitadosHtml = """{html}
        <table border='0' cellpadding='5' cellspacing='5' style='border-collapse: collapse; width: 90%; margin: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.85em;'>
        <tbody>
        <tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
                 <th>Código Clarity</th>
               	 <th>Actividad</th>
                 <th>Naturaleza</th>
                 <th>Cliente</th>
                 <th>Año 1</th>
                 <th>Año 2</th>
                 <th>Año 3</th>
                 <th>Año 4</th>
                 <th>Año 5</th>
				 <th>Planned</th>
             </tr>
             
             """
	*/		 
String solicitadosHtml = " "
if(totalSolicitado[0] != 0){
solicitadosHtml = """{html}
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
}

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
    if (isChangeRequest(issue.getCustomFieldValue(categoria_cf))){
    	def call = tgeGridDataManager.readGridData(issue.getId(), pep_existente, null, null, 0, tgeGridDataManager.getRowCount(issue.getId(),pep_existente,user), user);
    	for(HashMap row: call.getValues()){
		log.error("Row1: "+row);
		if (isChangeRequest(issue.getCustomFieldValue(categoria_cf))){
        aprobadosHtml 				+= setApproved(row);
		}
        //aprobadosSolicitadosHtml 	+= setApprovedAndRequested(row);
		solicitadosHtml 			+= setSolicitado(row)
        //log.error(row)
    	};
    def call2 = tgeGridDataManager.readGridData(issue.getId(), coste_one_time, null, null, 0, tgeGridDataManager.getRowCount(issue.getId(),coste_one_time,user), user);
    	for(HashMap row: call2.getValues()){
		log.error("Row2--> "+row);
        log.error("ROW year5-->"+row.year5)
        log.error('ROW year1'+row.year1)
        solicitadosHtml2 				+= setSolicitado2(row);
        log.error('1 - solicitadosHtml2-->'+solicitadosHtml2)
    	};   
    }
    else{
    def call2 = tgeGridDataManager.readGridData(issue.getId(), coste_one_time, null, null, 0, tgeGridDataManager.getRowCount(issue.getId(),coste_one_time,user), user);
    	for(HashMap row: call2.getValues()){
		log.error("Row2--> "+row);
        log.error("ROW year5-->"+row.year5)
        log.error('ROW year1'+row.year1)
        solicitadosHtml2 				+= setSolicitado2(row);
        log.error('2 - solicitadosHtml2-->'+solicitadosHtml2)
    	};
        //log.error(aprobadosHtml);
    }
}catch(Exception e){
    log.error(e.getMessage());
    log.error("No se puedo encontrar el valor del campo.");
}

//cierra el html de los campos.
if (isChangeRequest(issue.getCustomFieldValue(categoria_cf))){
aprobadosHtml 				+= setEndOfTable(totalAprobado, 2);
}
//aprobadosSolicitadosHtml 	+= setEndOfTable(totalAprobadoSolicitado, 3);
//Si es 0 no se pone
if(totalSolicitado[0] != 0){
solicitadosHtml 			+= setEndOfTable(totalSolicitado, 2);
}
solicitadosHtml2 			+= setEndOfTable(totalAprobado2, 5);
//log.error(aprobadosHtml);
//log.error(aprobadosSolicitadosHtml);


//setea los valores de los campos html y reindexa el issue
if (isChangeRequest(issue.getCustomFieldValue(categoria_cf))){
aprobados_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(aprobados_cf),aprobadosHtml),new DefaultIssueChangeHolder());
}
//aprobadosSolicitados_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(aprobadosSolicitados_cf),aprobadosSolicitadosHtml),new DefaultIssueChangeHolder());
solicitado_cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(solicitado_cf), solicitadosHtml+solicitadosHtml2),new DefaultIssueChangeHolder());

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

    totalAprobado2[0] += row.get("year1");
    totalAprobado2[1] += row.get("year2");
    totalAprobado2[2] += row.get("year3");
    totalAprobado2[3] += row.get("year4");
    totalAprobado2[4] += row.get("year5");

	log.error("Tipo Plan: ${row.get("tipoPlan")}")
    return """<tr style='border-bottom: 1px solid #ccc; line-height: 1.8em;'>
					 <td>${row.get("actividad").get("name")}</td>           
                     <td>${row.get("descripcion")}</td>
                     <td>${row.get("unidadit").get("name")}</td>
                     <td>${row.get("grupoart").get("name")}</td>
                     <td>${row.get("subservventa").get("name")}</td>
                     <td>${row.get("proveedor").get("name")}</td>
                     <td>${row.get("year1").round(2)}</td>
                     <td>${row.get("year2").round(2)}</td>
                     <td>${row.get("year3").round(2)}</td>
                     <td>${row.get("year4").round(2)}</td>
                     <td>${row.get("year5").round(2)}</td>
					 <td>${row.get("planned").get("name")}</td>
                    </tr>"""
}


//setea una fila del campo costes aprobados + solicitados (Costes existentes)
String setApprovedAndRequested(HashMap row){

    totalAprobadoSolicitado[0] += row.get("solicitadoY1") + row.get("aprobadoY1");
    totalAprobadoSolicitado[1] += row.get("solicitadoY2") + row.get("aprobadoY2");
    totalAprobadoSolicitado[2] += row.get("solicitadoY3") + row.get("aprobadoY3");
    totalAprobadoSolicitado[3] += row.get("solicitadoY4") + row.get("aprobadoY4");
    totalAprobadoSolicitado[4] += row.get("solicitadoY5") + row.get("aprobadoY5")
	log.error("Tipo Plan: ${row.get("tipoPlan")}")

    return """<tr>
					<td>${row.get("clarityID")}</td>
                    <td>${row.get("actividad")}</td>
                    <td>${row.get("naturaleza")}</td>
                    <td>${row.get("cliente")}</td>
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

    for (int i = 0; i < spaceNumber; i++) {
        endText += """<td></td>\n"""
    }
    endText += """<td>TOTAL:</td>
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
}

def isChangeRequest(def categoria){
    return categoria?.optionId == 10903L
}
}
