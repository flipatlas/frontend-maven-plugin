#!/bin/sh

export NVM_DIR="$PWD/.nvm";
echo "NVM_DIR set";

(
  git clone https://github.com/nvm-sh/nvm.git "$NVM_DIR" &&
  cd "$NVM_DIR" &&
  git checkout `git describe --abbrev=0 --tags --match "v[0-9]*" $(git rev-list --tags --max-count=1)`
);
echo "NVM checked out";

\. "$NVM_DIR/nvm.sh"
echo "NVM loaded";
