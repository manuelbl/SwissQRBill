#!/bin/sh
for src in actual*; do
  dest=${src#actual_*}
  mv ./$src ./src/test/resources/$dest
done
