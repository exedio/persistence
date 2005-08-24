alter table `CollisionItem2` drop foreign key `CollisItem2_colliAttri_Fk`
alter table `CollisionItem1` drop foreign key `CollisItem1_colliAttri_Fk`
alter table `SecondSub` drop foreign key `SecondSub_Sup`
alter table `FirstSub` drop foreign key `FirstSub_Sup`
alter table `PointerItem` drop foreign key `PointerItem_pointer_Fk`
alter table `PointerItem` drop foreign key `PointerItem_pointer2_Fk`
alter table `PointerItem` drop foreign key `PointerItem_self_Fk`
alter table `PointerItem` drop foreign key `PointerItem_empty2_Fk`
alter table `QualifiIntegerEnumQualifi` drop foreign key `QualiIntegEnumQuali_up_Fk`
alter table `QualifiedStringQualifier` drop foreign key `QualifStringQuali_pare_Fk`
alter table `QualifiedEmptyQualifier` drop foreign key `QualifEmptyQualif_pare_Fk`
alter table `QualifiedEmptyQualifier` drop foreign key `QualifiEmptyQualif_key_Fk`
alter table `AttributeEmptyItem` drop foreign key `AttribuEmptyItem_paren_Fk`
alter table `AttributeEmptyItem` drop foreign key `AttributeEmptyItem_key_Fk`
alter table `AttributeItem` drop foreign key `AttributeItem_someItem_Fk`
alter table `AttributeItem` drop foreign key `AttrItem_somNotNullIte_Fk`
drop table `CollisionItem2`
drop table `CollisionItem1`
drop table `SecondSub`
drop table `FirstSub`
drop table `Super`
drop table `PointerItem`
drop table `PointerTargetItem`
drop table `QualifiIntegerEnumQualifi`
drop table `QualifiedStringQualifier`
drop table `QualifiedEmptyQualifier`
drop table `QualifiedItem`
drop table `SumItem`
drop table `HttpEntityItem`
drop table `StringItem`
drop table `AttributeEmptyItem`
drop table `AttributeItem`
drop table `EmptyItem2`
drop table `EmptyItem`
drop table `ItemWithDoubleUnique`
drop table `ItemWithSinglUniquNotNull`
drop table `ItemWithSinglUniqReadOnly`
drop table `ItemWithSingleUnique`
