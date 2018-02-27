insert into plant_inventory_entry (id, name, description, price)
    values (1, 'Mini excavator', '1.5 Tonne Mini excavator', 150);
insert into plant_inventory_entry (id, name, description, price)
    values (2, 'Mini excavator', '3 Tonne Mini excavator', 200);
insert into plant_inventory_entry (id, name, description, price)
    values (3, 'Midi excavator', '5 Tonne Midi excavator', 250);
insert into plant_inventory_entry (id, name, description, price)
    values (4, 'Midi excavator', '8 Tonne Midi excavator', 300);
insert into plant_inventory_entry (id, name, description, price)
    values (5, 'Maxi excavator', '15 Tonne Large excavator', 400);
insert into plant_inventory_entry (id, name, description, price)
    values (6, 'Maxi excavator', '20 Tonne Large excavator', 450);
insert into plant_inventory_entry (id, name, description, price)
    values (7, 'HS dumper', '1.5 Tonne Hi-Swivel Dumper', 150);
insert into plant_inventory_entry (id, name, description, price)
    values (8, 'FT dumper', '2 Tonne Front Tip Dumper', 180);
insert into plant_inventory_entry (id, name, description, price)
    values (9, 'FT dumper', '2 Tonne Front Tip Dumper', 200);
insert into plant_inventory_entry (id, name, description, price)
    values (10, 'FT dumper', '2 Tonne Front Tip Dumper', 300);
insert into plant_inventory_entry (id, name, description, price)
    values (11, 'FT dumper', '3 Tonne Front Tip Dumper', 400);
insert into plant_inventory_entry (id, name, description, price)
    values (12, 'Loader', 'Hewden Backhoe Loader', 200);
insert into plant_inventory_entry (id, name, description, price)
    values (13, 'D-Truck', '15 Tonne Articulating Dump Truck', 250);
insert into plant_inventory_entry (id, name, description, price)
    values (14, 'D-Truck', '30 Tonne Articulating Dump Truck', 300);


-- -------------------------------------------------------------------------

insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (1, 1, 'A01');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (2, 2, 'A02');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (3, 3, 'A03');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (4, 4, 'A04');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (5, 6, 'A05');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (6, 6, 'A06');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (7, 7, 'A07');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (8, 8, 'A08');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (9, 9, 'A09');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (10, 10, 'A10');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (11, 11, 'A11');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (12, 12, 'A12');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (13, 13, 'A13');
insert into plant_inventory_item (id, plant_info_id, serial_number)
    values (14, 14, 'A14');



-- -------------------------------------------------------------------------

-- Year 2015  Mini- Reservations = 4
-- Year 2015  Midi- Reservations = 4
-- Year 2015  Maxi Reservations = 4
-- Year 2015  HS-Dumper Reservations = 2
-- Year 2015  FT-Dumper Reservations = 2
-- Year 2015  Loader Reservations = 2
-- Year 2015  D-Truck Reservations = 2


---------------------------------------------------------------

-- Year 2016  Loader Reservations = 1
-- Year 2016  D-Truck Reservations = 1


---------------------------------------------------------------



--

insert into plant_reservation (id, plant_id, start_date, end_date)
    values (1, 1, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (2, 1, '2015-04-22', '2015-04-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (3, 2, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (4, 2, '2015-04-22', '2015-04-24');


insert into plant_reservation (id, plant_id, start_date, end_date)
    values (5, 3, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (6, 3, '2015-04-22', '2015-04-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (7, 4, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (8, 4, '2015-04-22', '2015-04-24');



insert into plant_reservation (id, plant_id, start_date, end_date)
    values (9, 5, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (10, 5, '2015-04-22', '2015-04-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (11, 6, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (12, 6, '2015-04-22', '2015-04-24');


insert into plant_reservation (id, plant_id, start_date, end_date)
    values (13, 7, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (14, 7, '2015-04-22', '2015-04-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (15, 8, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (16, 8, '2015-04-22', '2015-04-24');


insert into plant_reservation (id, plant_id, start_date, end_date)
    values (17, 12, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (18, 12, '2015-04-22', '2015-04-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (19, 13, '2015-03-22', '2015-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (20, 13, '2015-04-22', '2015-04-24');


    ------------

insert into plant_reservation (id, plant_id, start_date, end_date)
    values (21, 12, '2016-03-22', '2016-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (22, 13, '2016-03-22', '2016-03-24');

    ------------
/*
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (21, 12, '2016-03-22', '2016-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date)
    values (22, 13, '2016-03-22', '2016-03-24');

    ------------*/


---------------------------------------------------------------


