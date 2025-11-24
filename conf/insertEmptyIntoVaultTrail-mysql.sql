-- Inserts a row for the empty blob into a vault trail.
--
-- You have to adapt the identifier "bucket" (part of VaultTrail_bucket)
-- to the concrete project and bucket.
--
-- Furthermore, you may have to adjust the SHA-512 hash (cf83e1357...) to the
-- actual value of connect property vault.algorithm, which is typically at its
-- default of SHA-512.

INSERT INTO `VaultTrail_bucket`
(`hash`,`length`,`start20`,`date`,`origin`)
VALUES(
'cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e',
0,x'',{ts '2023-01-11 16:10:00.000'},'insertEmptyIntoVaultTrail.sql');
