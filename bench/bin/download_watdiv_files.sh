#!/usr/bin/env bash

set -e

WATDIV_ARCHIVE="watdiv.10M.tar.bz2"
STRESS_ARCHIVE="stress-workloads.tar.gz"
TARGET_DIR="resources/watdiv/data"

mkdir -p ${TARGET_DIR}

if [ ! -f "${TARGET_DIR}/watdiv.10M.nt" ]; then
  # expired certificate
	wget https://dsg.uwaterloo.ca/watdiv/${WATDIV_ARCHIVE} --no-check-certificate
	echo "extracting: ${WATDIV_ARCHIVE}"
	tar -xvjf ${WATDIV_ARCHIVE} -C ${TARGET_DIR}
  rm ${WATDIV_ARCHIVE}
fi

if [ ! -f "${TARGET_DIR}/watdiv-stress-100" ]; then
  # expired certificate
	wget https://dsg.uwaterloo.ca/watdiv/${STRESS_ARCHIVE} --no-check-certificate
	echo "extracting: ${STRESS_ARCHIVE}"
	tar -xvzf ${STRESS_ARCHIVE} -C ${TARGET_DIR}
  rm ${STRESS_ARCHIVE}
fi
