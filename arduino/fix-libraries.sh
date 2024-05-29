#!/usr/bin/env bash

set -euo pipefail

sed -i 's/^   int attempts(0)/\/*   int attempts(0)/' "$HOME/.arduino15/internal/ArduinoHttpServer_0.10.0_b5ccee01019a5f88/ArduinoHttpServer/src/internals/StreamHttpRequest.hpp" && sed -i 's/Read complete line.$/Read complete line.*\//' "$HOME/.arduino15/internal/ArduinoHttpServer_0.10.0_b5ccee01019a5f88/ArduinoHttpServer/src/internals/StreamHttpRequest.hpp"


