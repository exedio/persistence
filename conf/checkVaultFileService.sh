# VaultFileService hash checker
# Generates a file suitable for
#   sha512sum --check --quiet --strict
find . \
	-mindepth 2 \
	-maxdepth 2 \
	-not -path "./.tempVaultFileService/*" \
	-not -path "./VaultBucketTag/*" \
	-not -path "./VaultGenuineServiceKey/*" \
	-type f \
	| sed -e 's|\(\./\([0-9a-f]*\)/\([0-9a-f]*\)\)|\2\3  \1|'
