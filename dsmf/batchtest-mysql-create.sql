create table `ItemWithSingleUnique`(`this` integer,`uniqueString` varchar(255) binary,`otherString` varchar(255) binary,constraint `ItemWithSingleUnique_Pk` primary key(`this`),constraint `ItemWithSingUni_unStr_Unq` unique(`uniqueString`)) engine=innodb
create table `ItemWithSinglUniqReadOnly`(`this` integer,`uniqueReadOnlyString` varchar(255) binary,constraint `ItemWithSingUniqReaOnl_Pk` primary key(`this`),constraint `IteWitSiUnReOn_uReOnSt_Un` unique(`uniqueReadOnlyString`)) engine=innodb
create table `ItemWithSinglUniquNotNull`(`this` integer,`uniqueNotNullString` varchar(255) binary,constraint `ItemWithSingUniqNotNul_Pk` primary key(`this`),constraint `IteWitSiUnNoNu_uNoNuSt_Un` unique(`uniqueNotNullString`)) engine=innodb
create table `ItemWithDoubleUnique`(`this` integer,`string` varchar(255) binary,`integer` integer,constraint `ItemWithDoubleUnique_Pk` primary key(`this`),constraint `ItemWithDoubUni_doUni_Unq` unique(`string`,`integer`)) engine=innodb
create table `EmptyItem`(`this` integer,constraint `EmptyItem_Pk` primary key(`this`)) engine=innodb
create table `EmptyItem2`(`this` integer,constraint `EmptyItem2_Pk` primary key(`this`)) engine=innodb
create table `AttributeItem`(`this` integer,`someString` varchar(255) binary,`someNotNullString` varchar(255) binary,`someInteger` integer,`someNotNullInteger` integer,`someLong` bigint,`someNotNullLong` bigint,`someDouble` double,`someNotNullDouble` double,`someDate` bigint,`someLongDate` bigint,`someBoolean` integer,`someNotNullBoolean` integer,`someItem` integer,`someNotNullItem` integer,`someEnum` integer,`someNotNullEnum` integer,`someDataMajor` varchar(30) binary,`someDataMinor` varchar(30) binary,constraint `AttributeItem_Pk` primary key(`this`)) engine=innodb
create table `AttributeEmptyItem`(`this` integer,`parent` integer,`key` integer,`someQualifiedString` varchar(255) binary,constraint `AttributeEmptyItem_Pk` primary key(`this`),constraint `AttriEmptyItem_parKey_Unq` unique(`parent`,`key`)) engine=innodb
create table `StringItem`(`this` integer,`any` varchar(255) binary,`min4` varchar(255) binary,`max4` varchar(4) binary,`min4Max8` varchar(8) binary,`exact6` varchar(6) binary,constraint `StringItem_Pk` primary key(`this`)) engine=innodb
create table `HttpEntityItem`(`this` integer,`fileMajor` varchar(30) binary,`fileMinor` varchar(30) binary,`imageMinor` varchar(30) binary,`photoExists` integer,constraint `HttpEntityItem_Pk` primary key(`this`)) engine=innodb
create table `SumItem`(`this` integer,`num1` integer,`num2` integer,`num3` integer,constraint `SumItem_Pk` primary key(`this`)) engine=innodb
create table `QualifiedItem`(`this` integer,`number` integer,constraint `QualifiedItem_Pk` primary key(`this`)) engine=innodb
create table `QualifiedEmptyQualifier`(`this` integer,`parent` integer,`key` integer,`qualifiedA` varchar(255) binary,`qualifiedB` varchar(255) binary,constraint `QualifiedEmptyQualifie_Pk` primary key(`this`),constraint `QualiEmptQual_quaUniq_Unq` unique(`parent`,`key`)) engine=innodb
create table `QualifiedStringQualifier`(`this` integer,`parent` integer,`key` varchar(255) binary,`qualifiedA` integer,`qualifiedB` integer,constraint `QualifieStringQualifie_Pk` primary key(`this`),constraint `QualiStriQual_quaUniq_Unq` unique(`parent`,`key`)) engine=innodb
create table `QualifiIntegerEnumQualifi`(`this` integer,`up` integer,`keyX` integer,`keyY` integer,`qualifiedA` varchar(255) binary,`qualifiedB` varchar(255) binary,constraint `QualifIntegeEnumQualif_Pk` primary key(`this`),constraint `QualInteEnumQua_quUni_Unq` unique(`up`,`keyX`,`keyY`)) engine=innodb
create table `PointerTargetItem`(`this` integer,`code` varchar(255) binary,constraint `PointerTargetItem_Pk` primary key(`this`)) engine=innodb
create table `PointerItem`(`this` integer,`code` varchar(255) binary,`pointer` integer,`pointer2` integer,`self` integer,`empty2` integer,constraint `PointerItem_Pk` primary key(`this`)) engine=innodb
create table `Super`(`this` integer,`class` varchar(255) binary,`superInt` integer,`superString` varchar(255) binary,constraint `Super_Pk` primary key(`this`)) engine=innodb
create table `FirstSub`(`this` integer,`firstSubString` varchar(255) binary,constraint `FirstSub_Pk` primary key(`this`)) engine=innodb
create table `SecondSub`(`this` integer,`firstSubString` varchar(255) binary,constraint `SecondSub_Pk` primary key(`this`)) engine=innodb
create table `CollisionItem1`(`this` integer,`collisionAttribute` integer,constraint `CollisionItem1_Pk` primary key(`this`),constraint `CollisItem1_collAttri_Unq` unique(`collisionAttribute`)) engine=innodb
create table `CollisionItem2`(`this` integer,`collisionAttribute` integer,constraint `CollisionItem2_Pk` primary key(`this`),constraint `CollisItem2_collAttri_Unq` unique(`collisionAttribute`)) engine=innodb
alter table `AttributeItem` add constraint `AttributeItem_someItem_Fk` foreign key (`someItem`) references `EmptyItem`(`this`)
alter table `AttributeItem` add constraint `AttrItem_somNotNullIte_Fk` foreign key (`someNotNullItem`) references `EmptyItem`(`this`)
alter table `AttributeEmptyItem` add constraint `AttribuEmptyItem_paren_Fk` foreign key (`parent`) references `AttributeItem`(`this`)
alter table `AttributeEmptyItem` add constraint `AttributeEmptyItem_key_Fk` foreign key (`key`) references `EmptyItem`(`this`)
alter table `QualifiedEmptyQualifier` add constraint `QualifEmptyQualif_pare_Fk` foreign key (`parent`) references `QualifiedItem`(`this`)
alter table `QualifiedEmptyQualifier` add constraint `QualifiEmptyQualif_key_Fk` foreign key (`key`) references `EmptyItem`(`this`)
alter table `QualifiedStringQualifier` add constraint `QualifStringQuali_pare_Fk` foreign key (`parent`) references `QualifiedItem`(`this`)
alter table `QualifiIntegerEnumQualifi` add constraint `QualiIntegEnumQuali_up_Fk` foreign key (`up`) references `QualifiedItem`(`this`)
alter table `PointerItem` add constraint `PointerItem_pointer_Fk` foreign key (`pointer`) references `PointerTargetItem`(`this`)
alter table `PointerItem` add constraint `PointerItem_pointer2_Fk` foreign key (`pointer2`) references `PointerTargetItem`(`this`)
alter table `PointerItem` add constraint `PointerItem_self_Fk` foreign key (`self`) references `PointerItem`(`this`)
alter table `PointerItem` add constraint `PointerItem_empty2_Fk` foreign key (`empty2`) references `EmptyItem2`(`this`)
alter table `FirstSub` add constraint `FirstSub_Sup` foreign key (`this`) references `Super`(`this`)
alter table `SecondSub` add constraint `SecondSub_Sup` foreign key (`this`) references `Super`(`this`)
alter table `CollisionItem1` add constraint `CollisItem1_colliAttri_Fk` foreign key (`collisionAttribute`) references `EmptyItem`(`this`)
alter table `CollisionItem2` add constraint `CollisItem2_colliAttri_Fk` foreign key (`collisionAttribute`) references `EmptyItem`(`this`)
