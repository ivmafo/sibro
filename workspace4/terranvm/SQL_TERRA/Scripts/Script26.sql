-----------------------------------------Script 25-----------------------------------------------------
-------------------------------------jlopez 14-09-2012--------------------------------------------------
-- Ejecutar este comando cuando no existan activos pendientes por aprobar. De lo contrario puede tener inconsistensias al editar o crear una hoja de términos

update real_property set pending_approve=false;

