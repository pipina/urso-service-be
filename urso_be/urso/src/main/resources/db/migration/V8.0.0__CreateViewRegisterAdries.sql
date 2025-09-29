CREATE VIEW mv_ra_internal_1_index
AS
WITH max_seq AS (SELECT zaznam_id,
                        kluc,
                        MAX(sekvencia) AS sekvencia
                 FROM ra_internal_1_index
                 WHERE kluc IN (
                                'RegionItemCode', 'RegionItemName', 'addressType', 'CountyItemCode', 'CountyItemName',
                                'regionId', 'MunicipalityItemCode', 'MunicipalityItemName', 'countyId',
                                'DistrictItemCode',
                                'DistrictItemName', 'municipalityId', 'districtId', 'PropertyRegistrationNumber',
                                'StreetName', 'streetNameId', 'PropertyRegistrationNumberIdr', 'BuildingIndex',
                                'BuildingNumber', 'PostalCode', 'BuildingNumberId'
                     )
                 GROUP BY zaznam_id, kluc)
SELECT dat.id,
       dat.zaznam_id,
       dat.kluc,
       dat.hodnota,
       dat.hodnota_zjednodusena,
       dat.ucinnost_od,
       dat.ucinnost_do,
       dat.sekvencia,
       dat.aktualny
FROM ra_internal_1_index AS dat
         JOIN max_seq
              ON (
                          dat.zaznam_id = max_seq.zaznam_id
                      AND dat.kluc = max_seq.kluc
                      AND dat.sekvencia = max_seq.sekvencia
                  );
/*CREATE INDEX indx_mv_ra_internal_1_value ON mv_ra_internal_1_index (value, kluc);
CREATE INDEX indx_mv_ra_internal_1_key_id ON mv_ra_internal_1_index (kluc, zaznam_id);*/

CREATE VIEW mv_ra_internal_1_region
AS
SELECT main.zaznam_id                AS region_entry_id,
       main.hodnota                  AS "change",
       itemcode.hodnota              AS region_item_code,
       itemname.hodnota              AS region_item_name,
       itemname.hodnota_zjednodusena AS region_item_name_simplified,
       itemcode.ucinnost_od          AS region_item_code_effective_from,
       itemname.ucinnost_od          AS region_item_name_effective_from,
       itemcode.ucinnost_do          AS region_item_code_effective_to,
       itemname.ucinnost_do          AS region_item_name_effective_to,
       itemcode.aktualny             AS region_item_code_current,
       itemname.aktualny             AS region_item_name_current,
       itemcode.sekvencia            AS region_item_code_sequence,
       itemname.sekvencia            AS region_item_name_sequence
FROM mv_ra_internal_1_index AS main
         LEFT OUTER JOIN mv_ra_internal_1_index AS itemcode
                         ON (
                                     main.zaznam_id = itemcode.zaznam_id
                                 AND itemcode.kluc = 'RegionItemCode'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS itemname
                         ON (
                                     main.zaznam_id = itemname.zaznam_id
                                 AND itemname.kluc = 'RegionItemName'
                             )
WHERE (
                  main.kluc = 'addressType'
              AND main.hodnota = 'REGION'
          );
/*CREATE INDEX mv_ra_internal_1_region_zaznam_id ON mv_ra_internal_1_region (region_zaznam_id);*/

CREATE VIEW mv_ra_internal_1_county
AS
SELECT main.zaznam_id                AS county_entry_id,
       parent.hodnota                AS parent_region_entry_id,
       itemcode.hodnota              AS county_item_code,
       itemname.hodnota              AS county_item_name,
       itemname.hodnota_zjednodusena AS county_item_name_simplified,
       itemcode.ucinnost_od          AS county_item_code_effective_from,
       itemname.ucinnost_od          AS county_item_name_effective_from,
       itemcode.ucinnost_do          AS county_item_code_effective_to,
       itemname.ucinnost_do          AS county_item_name_effective_to,
       itemcode.aktualny             AS county_item_code_current,
       itemname.aktualny             AS county_item_name_current,
       itemcode.sekvencia            AS county_item_code_sequence,
       itemname.sekvencia            AS county_item_name_sequence
FROM mv_ra_internal_1_index AS main
         LEFT OUTER JOIN mv_ra_internal_1_index AS itemcode
                         ON (
                                     main.zaznam_id = itemcode.zaznam_id
                                 AND itemcode.kluc = 'CountyItemCode'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS itemname
                         ON (
                                     main.zaznam_id = itemname.zaznam_id
                                 AND itemname.kluc = 'CountyItemName'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent
                         ON (
                                     main.zaznam_id = parent.zaznam_id
                                 AND parent.kluc = 'regionId'
                             )
WHERE (
                  main.kluc = 'addressType'
              AND main.hodnota = 'COUNTY'
          );
/*CREATE INDEX mv_ra_internal_1_county_entry ON mv_ra_internal_1_county (county_zaznam_id);
CREATE INDEX mv_ra_internal_1_county_parent ON mv_ra_internal_1_county (parent_region_zaznam_id);*/

CREATE VIEW mv_ra_internal_1_municipality
AS
SELECT main.zaznam_id                AS municipality_entry_id,
       parent.hodnota                AS parent_county_entry_id,
       itemcode.hodnota              AS municipality_item_code,
       itemname.hodnota              AS municipality_item_name,
       itemname.hodnota_zjednodusena AS municipality_item_name_simplified,
       itemcode.ucinnost_od          AS municipality_item_code_effective_from,
       itemname.ucinnost_od          AS municipality_item_name_effective_from,
       itemcode.ucinnost_do          AS municipality_item_code_effective_to,
       itemname.ucinnost_do          AS municipality_item_name_effective_to,
       itemcode.aktualny             AS municipality_item_code_current,
       itemname.aktualny             AS municipality_item_name_current,
       itemcode.sekvencia            AS municipality_item_code_sequence,
       itemname.sekvencia            AS municipality_item_name_sequence
FROM mv_ra_internal_1_index AS main
         LEFT OUTER JOIN mv_ra_internal_1_index AS itemcode
                         ON (
                                     main.zaznam_id = itemcode.zaznam_id
                                 AND itemcode.kluc = 'MunicipalityItemCode'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS itemname
                         ON (
                                     main.zaznam_id = itemname.zaznam_id
                                 AND itemname.kluc = 'MunicipalityItemName'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent
                         ON (
                                     main.zaznam_id = parent.zaznam_id
                                 AND parent.kluc = 'countyId'
                             )
WHERE (
                  main.kluc = 'addressType'
              AND main.hodnota = 'MUNICIPALITY'
          );
/*CREATE INDEX mv_ra_internal_1_municipality_entry ON mv_ra_internal_1_municipality (municipality_zaznam_id);
CREATE INDEX mv_ra_internal_1_municipality_parent ON mv_ra_internal_1_municipality (parent_county_zaznam_id);*/

CREATE VIEW mv_ra_internal_1_district
AS
SELECT main.zaznam_id                AS district_entry_id,
       parent.hodnota                AS parent_municipality_entry_id,
       itemcode.hodnota              AS district_item_code,
       itemname.hodnota              AS district_item_name,
       itemname.hodnota_zjednodusena AS district_item_name_simplified,
       itemcode.ucinnost_od          AS district_item_code_effective_from,
       itemname.ucinnost_od          AS district_item_name_effective_from,
       itemcode.ucinnost_do          AS district_item_code_effective_to,
       itemname.ucinnost_do          AS district_item_name_effective_to,
       itemcode.aktualny             AS district_item_code_current,
       itemname.aktualny             AS district_item_name_current,
       itemcode.sekvencia            AS district_item_code_sequence,
       itemname.sekvencia            AS district_item_name_sequence
FROM mv_ra_internal_1_index AS main
         LEFT OUTER JOIN mv_ra_internal_1_index AS itemcode
                         ON (
                                     main.zaznam_id = itemcode.zaznam_id
                                 AND itemcode.kluc = 'DistrictItemCode'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS itemname
                         ON (
                                     main.zaznam_id = itemname.zaznam_id
                                 AND itemname.kluc = 'DistrictItemName'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent
                         ON (
                                     main.zaznam_id = parent.zaznam_id
                                 AND parent.kluc = 'municipalityId'
                             )
WHERE (
                  main.kluc = 'addressType'
              AND main.hodnota = 'DISTRICT'
          );
/*CREATE INDEX mv_ra_internal_1_district_entry ON mv_ra_internal_1_district (district_zaznam_id);
CREATE INDEX mv_ra_internal_1_district_parent ON mv_ra_internal_1_district (parent_municipality_zaznam_id);*/

CREATE VIEW mv_ra_internal_1_property_registration_number
AS
SELECT main.zaznam_id   AS property_registration_number_entry_id,
       parent_m.hodnota AS parent_municipality_entry_id,
       parent_d.hodnota AS parent_district_entry_id,
       prn.zaznam_id    AS property_registration_number_id,
       prn.hodnota      AS property_registration_number,
       prn.ucinnost_od  AS property_registration_number_ucinnost_od,
       prn.ucinnost_do  AS property_registration_number_ucinnost_do,
       prn.aktualny     AS property_registration_number_current,
       prn.sekvencia    AS property_registration_number_sequence
FROM mv_ra_internal_1_index AS main
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent_m
                         ON (
                                     main.zaznam_id = parent_m.zaznam_id
                                 AND parent_m.kluc = 'municipalityId'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent_d
                         ON (
                                     main.zaznam_id = parent_d.zaznam_id
                                 AND parent_d.kluc = 'districtId'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS prn
                         ON (
                                     main.zaznam_id = prn.zaznam_id
                                 AND prn.kluc = 'PropertyRegistrationNumber'
                             )
WHERE (
                  main.kluc = 'addressType'
              AND main.hodnota = 'PROPERTY_REGISTRATION_NUMBER'
          );
/*CREATE INDEX mv_ra_internal_1_property_registration_number_entry ON mv_ra_internal_1_property_registration_number (property_registration_number_zaznam_id);
CREATE INDEX mv_ra_internal_1_property_registration_number_parent_m ON mv_ra_internal_1_property_registration_number (parent_municipality_zaznam_id);
CREATE INDEX mv_ra_internal_1_property_registration_number_parent_d ON mv_ra_internal_1_property_registration_number (parent_district_zaznam_id);*/

CREATE VIEW mv_ra_internal_1_street_name
AS
SELECT main.zaznam_id           AS street_name_entry_id,
       parent_m.hodnota         AS parent_municipality_entry_id,
       parent_d.hodnota         AS parent_district_entry_id,
       str.zaznam_id            AS street_name_id,
       str.hodnota              AS street_name,
       str.hodnota_zjednodusena AS street_name_simplified,
       str.ucinnost_od          AS street_name_ucinnost_od,
       str.ucinnost_do          AS street_name_ucinnost_do,
       str.aktualny             AS street_name_current,
       str.sekvencia            AS street_name_sequence
FROM mv_ra_internal_1_index AS main
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent_m
                         ON (
                                     main.zaznam_id = parent_m.zaznam_id
                                 AND parent_m.kluc = 'municipalityId'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent_d
                         ON (
                                     main.zaznam_id = parent_d.zaznam_id
                                 AND parent_d.kluc = 'districtId'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS str
                         ON (
                                     main.zaznam_id = str.zaznam_id
                                 AND str.kluc = 'StreetName'
                             )
WHERE (
                  main.kluc = 'addressType'
              AND main.hodnota = 'STREET_NAME'
          );
/*CREATE INDEX mv_ra_internal_1_street_name_entry ON mv_ra_internal_1_street_name (street_name_zaznam_id);
CREATE INDEX mv_ra_internal_1_street_name_parent_m ON mv_ra_internal_1_street_name (parent_municipality_zaznam_id);
CREATE INDEX mv_ra_internal_1_street_name_parent_d ON mv_ra_internal_1_street_name (parent_district_zaznam_id);*/

CREATE VIEW mv_ra_internal_1_building_number
AS
SELECT main.zaznam_id          AS building_number_entry_id,
       parent_s.hodnota        AS parent_street_name_entry_id,
       parent_p.hodnota        AS parent_property_registration_number_entry_id,
       bi.hodnota              AS building_index,
       bi.hodnota_zjednodusena AS building_index_simplified,
       bi.ucinnost_od          AS building_index_ucinnost_od,
       bi.ucinnost_do          AS building_index_ucinnost_do,
       bi.aktualny             AS building_index_current,
       bi.sekvencia            AS building_index_sequence,
       bn.zaznam_id            AS building_number_id,
       bn.hodnota              AS building_number,
       bn.hodnota_zjednodusena AS building_number_simplified,
       bn.ucinnost_od          AS building_number_ucinnost_od,
       bn.ucinnost_do          AS building_number_ucinnost_do,
       bn.aktualny             AS building_number_current,
       bn.sekvencia            AS building_number_sequence,
       pc.hodnota              AS postal_code,
       pc.hodnota_zjednodusena AS postal_code_simplified,
       pc.ucinnost_od          AS postal_code_ucinnost_od,
       pc.ucinnost_do          AS postal_code_ucinnost_do,
       pc.aktualny             AS postal_code_current,
       pc.sekvencia            AS postal_code_sequence
FROM mv_ra_internal_1_index AS main
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent_s
                         ON (
                                     main.zaznam_id = parent_s.zaznam_id
                                 AND parent_s.kluc = 'streetNameId'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent_p
                         ON (
                                     main.zaznam_id = parent_p.zaznam_id
                                 AND parent_p.kluc = 'PropertyRegistrationNumberIdr'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS bi
                         ON (
                                     main.zaznam_id = bi.zaznam_id
                                 AND bi.kluc = 'BuildingIndex'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS bn
                         ON (
                                     main.zaznam_id = bn.zaznam_id
                                 AND bn.kluc = 'BuildingNumber'
                             )
         LEFT OUTER JOIN mv_ra_internal_1_index AS pc
                         ON (
                                     main.zaznam_id = pc.zaznam_id
                                 AND pc.kluc = 'PostalCode'
                             )
WHERE (
                  main.kluc = 'addressType'
              AND main.hodnota = 'BUILDING_NUMBER'
          );
/*CREATE INDEX mv_ra_internal_1_building_number_entry ON mv_ra_internal_1_building_number (building_number_zaznam_id);
CREATE INDEX mv_ra_internal_1_building_number_parent_s ON mv_ra_internal_1_building_number (parent_street_name_zaznam_id);
CREATE INDEX mv_ra_internal_1_building_number_parent_p ON mv_ra_internal_1_building_number (parent_property_registration_number_zaznam_id);*/

CREATE VIEW mv_ra_internal_1_building_unit
AS
SELECT main.zaznam_id AS building_unit_entry_id,
       parent.hodnota AS parent_building_number_entry_id
FROM mv_ra_internal_1_index AS main
         LEFT OUTER JOIN mv_ra_internal_1_index AS parent
                         ON (
                                     main.zaznam_id = parent.zaznam_id
                                 AND parent.kluc = 'BuildingNumberId'
                             )
WHERE (
                  main.kluc = 'addressType'
              AND main.hodnota = 'BUILDING_UNIT'
          );
/*CREATE INDEX mv_ra_internal_1_building_unit_entry ON mv_ra_internal_1_building_unit (building_unit_zaznam_id);
CREATE INDEX mv_ra_internal_1_building_unit_parent ON mv_ra_internal_1_building_unit (parent_building_number_zaznam_id);*/