		
def arrayCostesInterno = customFieldManager.getCustomFieldObject("customfield_12215")	
def cfCostesValue = issue.getCustomFieldValue(cfCostes);	
def codClarityField = customFieldManager.getCustomFieldObject("customfield_11720")	
def cfCategoria = customFieldManager.getCustomFieldObject('customfield_11500')	
def categoria = issue.getCustomFieldValue(cfCategoria)	
def cfRefrescoCostes = customFieldManager.getCustomFieldObject("customfield_14808")	
def refrescoCostesValue = issue.getCustomFieldValue(cfRefrescoCostes)	
def cfForzarRefrescoCostes = customFieldManager.getCustomFieldObject("customfield_15205")	
def forzarRefrescoCostesValue = issue.getCustomFieldValue(cfForzarRefrescoCostes)	
def count = 0	
if(categoria.toString().contains("Change request")){	
    def actividades = issue.getSubTaskObjects().stream()	
            .filter{subtask ->subtask.getIssueType().getId() == "10206"}	
            .filter{subtask ->subtask.getStatusId() == "10911"}	
    for (actividad in actividades){	
        def codClarityValueAC = actividad.getCustomFieldValue(codClarityField)	
        if (codClarityValueAC == null){	
            count++	
        }	
    }	
    if (count <= 0) {	
        log.error("Change request sin actividades nuevas, no hace falta poner costes.")	
    }	
}	
def costesGrid = readGridData(issue, "customfield_11778")	
for (int rowNumber = 0; rowNumber < costesGrid.size(); rowNumber++) {	
    Map<String, Object> rowData = costesGrid.get(rowNumber)	
    if ((rowData?.naturaleza?.value == null || rowData?.naturaleza?.value?.isEmpty()) || (rowData?.capexopex == null || rowData?.capexopex?.isEmpty())){	
        throw new InvalidInputException("Naturaleza no informada. Debido a un problema en la introducción de datos este campo no está informado. Modifique las lineas de coste seleccionando la linea y editando con el lápiz de la tabla")	
    }	
}	
if (count > 0){	
    if(cfCostesValue != null){	
        def nRows = cfCostesValue as int;	
        log.warn('COMPROBANDO COSTES '+nRows);	
        if(nRows < 1){	
            throw new InvalidInputException('Debes insertar Costes');	
        }	
    } else {	
        log.warn('CAMPO Costes NULL ');	
        throw new InvalidInputException('El campo Costes es obligatorio');	
    }	
}	
if (issue.getCustomFieldValue(arrayCostesInterno) == null){	
    throw new InvalidInputException("Debes complimentar costes");	
}	
if(refrescoCostesValue != null && refrescoCostesValue == "1"){	
    throw new InvalidInputException("Has refrescado los costes. Debes informar los costes otra vez");	
}	
if (forzarRefrescoCostesValue != null && forzarRefrescoCostesValue == "Debe Recargar Costes"){	
    throw new InvalidInputException("Debes Recargar Costes");	
}	
private readGridData(MutableIssue issue, String fieldID) throws Exception {	
    List<Map<String, Object>> gridDataList = new ArrayList<Map<String, Object>>();	
    def tgeCustomField = customFieldManager.getCustomFieldObject(fieldID);	
    Long tgeCustomFieldId = tgeCustomField.getIdAsLong();	
    def callResult = tgeGridDataManager.readGridData(issue.getId(), tgeCustomFieldId, null, null, 0, tgeGridDataManager.getRowCount(issue.id, tgeCustomFieldId, user), user);	
    gridDataList = callResult.getValues();	
    return gridDataList	
}	