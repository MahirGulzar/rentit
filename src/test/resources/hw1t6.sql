--the available plant entries
insert into plant_inventory_entry (id, name, description, price)
  values ('1', 'Mini excavator', '1.5 Tonne Mini excavator', 150);
insert into plant_inventory_entry (id, name, description, price)
  values ('2', 'Mini excavator', '3 Tonne Mini excavator', 200);
insert into plant_inventory_entry (id, name, description, price)
  values ('3', 'Midi excavator', '5 Tonne Midi excavator', 250);
insert into plant_inventory_entry (id, name, description, price)
  values ('4', 'Midi excavator', '8 Tonne Midi excavator', 300);
insert into plant_inventory_entry (id, name, description, price)
  values ('5', 'Maxi excavator', '15 Tonne Large excavator', 400);
insert into plant_inventory_entry (id, name, description, price)
  values ('6', 'Maxi excavator', '20 Tonne Large excavator', 450);
insert into plant_inventory_entry (id, name, description, price)
  values ('7', 'HS dumper', '1.5 Tonne Hi-Swivel Dumper', 150);
insert into plant_inventory_entry (id, name, description, price)
  values ('8', 'FT dumper', '2 Tonne Front Tip Dumper', 180);
insert into plant_inventory_entry (id, name, description, price)
  values ('9', 'FT dumper', '2 Tonne Front Tip Dumper', 200);
insert into plant_inventory_entry (id, name, description, price)
  values ('10', 'FT dumper', '2 Tonne Front Tip Dumper', 300);
insert into plant_inventory_entry (id, name, description, price)
  values ('11', 'FT dumper', '3 Tonne Front Tip Dumper', 400);
insert into plant_inventory_entry (id, name, description, price)
  values ('12', 'Loader', 'Hewden Backhoe Loader', 200);
insert into plant_inventory_entry (id, name, description, price)
  values ('13', 'D-Truck', '15 Tonne Articulating Dump Truck', 250);
insert into plant_inventory_entry (id, name, description, price)
  values ('14', 'D-Truck', '30 Tonne Articulating Dump Truck', 300);

--the available physical plants
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('1', '1', 'A01', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('2', '2', 'A02', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('3', '3', 'A03', 'UNSERVICEABLE_REPAIRABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('4', '1', 'A04', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('5', '1', 'A05', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('6', '7', 'A06', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('7', '8', 'A07', 'UNSERVICEABLE_REPAIRABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('8', '9', 'A08', 'UNSERVICEABLE_INCOMPLETE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('9', '10', 'A09', 'UNSERVICEABLE_CONDEMNED');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('10', '13', 'A10', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values ('11', '14', 'A11', 'SERVICEABLE');
--todo add a lot of items of different kind.

--each physical plant has a maintenance plan for the year.
insert into maintenance_plan (id, year, plant_id) values ('1', 2017, '1');
insert into maintenance_plan (id, year, plant_id) values ('2', 2017, '2');
insert into maintenance_plan (id, year, plant_id) values ('3', 2017, '3');
insert into maintenance_plan (id, year, plant_id) values ('4', 2017, '4');
insert into maintenance_plan (id, year, plant_id) values ('5', 2017, '5');
insert into maintenance_plan (id, year, plant_id) values ('6', 2018, '1');
insert into maintenance_plan (id, year, plant_id) values ('7', 2018, '2');
insert into maintenance_plan (id, year, plant_id) values ('8', 2018, '3');
insert into maintenance_plan (id, year, plant_id) values ('9', 2018, '4');
insert into maintenance_plan (id, year, plant_id) values ('10', 2018, '5');
insert into maintenance_plan (id, year, plant_id) values ('11', 2018, '6');
insert into maintenance_plan (id, year, plant_id) values ('12', 2018, '7');

--maintenance for physical plants
insert into plant_reservation (id, plant_id, start_date, end_date) values ('1', '1', '2018-02-11', '2018-02-12');
insert into plant_reservation (id, plant_id, start_date, end_date) values ('2', '2', '2018-03-22', '2018-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date) values ('3', '3', '2018-03-02', '2018-03-04');
insert into plant_reservation (id, plant_id, start_date, end_date) values ('4', '1', '2017-02-11', '2017-02-12');
insert into plant_reservation (id, plant_id, start_date, end_date) values ('5', '2', '2018-03-25', '2018-03-26');
insert into plant_reservation (id, plant_id, start_date, end_date) values ('6', '6', '2018-03-10', '2018-03-20');
insert into plant_reservation (id, plant_id, start_date, end_date) values ('7', '10', '2018-03-10', '2018-03-20');

--the maintenance can be purchase orders or maintenance tasks. Each reservation must have one (not both).

-- insert into purchase_order (id, issue_date, payment_schedule, total, start_date, end_date, status, plant_id) values ('1', '2018-02-10', '2018-02-13', 200, '2017-02-11', '2017-02-12', 'CLOSED', '1');

insert into maintenance_task (id, reservation_id, maintenance_plan_id, type, description, price, start_date, end_date) values ('1', '2', '7', 'PREVENTIVE', 'fix', 200, '2018-03-22', '2018-03-24');
insert into maintenance_task (id, reservation_id, maintenance_plan_id, type, description, price, start_date, end_date) values ('2', '3', '8', 'CORRECTIVE', 'fix', 200, '2018-03-02', '2018-03-04');
insert into maintenance_task (id, reservation_id, maintenance_plan_id, type, description, price, start_date, end_date) values ('3', '4', '1', 'PREVENTIVE', 'fix', 200, '2017-02-11', '2017-02-12');
insert into maintenance_task (id, reservation_id, maintenance_plan_id, type, description, price, start_date, end_date) values ('4', '5', '7', 'CORRECTIVE', 'fix', 200, '2017-02-11', '2017-02-12');
insert into maintenance_task (id, reservation_id, maintenance_plan_id, type, description, price, start_date, end_date) values ('5', '6', '11', 'PREVENTIVE', 'fix', 200, '2018-03-10', '2018-03-20');
insert into maintenance_task (id, reservation_id, maintenance_plan_id, type, description, price, start_date, end_date) values ('6', '7', '12', 'CORRECTIVE', 'fix', 200, '2018-03-10', '2018-03-20');

--connects purchase order with appropriate reservation(s). Have to do it this way because of extensions.
-- insert into purchase_order_reservations (purchase_order_id, reservations_id) values ('1','1');

--connects maintenance tasks to plans - not needed with mappedBy and CascadeType.REMOVE TODO maybe do the same to PurchaseOrder
--insert into maintenance_plan_maintenance_tasks (maintenance_plan_id, maintenance_tasks_id) values ('7', '1');
--insert into maintenance_plan_maintenance_tasks (maintenance_plan_id, maintenance_tasks_id) values ('8', '2');
--insert into maintenance_plan_maintenance_tasks (maintenance_plan_id, maintenance_tasks_id) values ('1', '3');
--insert into maintenance_plan_maintenance_tasks (maintenance_plan_id, maintenance_tasks_id) values ('7', '4');
--todo add more purchase orders, maintenance plans and maintenance.