# Copyright 2020-2021 NXP
DESCRIPTION = "TensorFlow Lite VX Delegate"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7d6260e4f3f6f85de05af9c8f87e6fb5"

DEPENDS = "tensorflow-lite tim-vx"

require tensorflow-lite-${PV}.inc

TENSORFLOW_LITE_VX_DELEGATE_SRC ?= "git://github.com/nxp-imx/tflite-vx-delegate-imx.git;protocol=https" 
SRCBRANCH_vx = "lf-6.1.55_2.2.0"
SRCREV_vx = "c8a9737c63fddc3841d58b27a238b647ab51b8b9"

SRCREV_FORMAT = "vx_tf"

SRC_URI = "${TENSORFLOW_LITE_VX_DELEGATE_SRC};branch=${SRCBRANCH_vx};name=vx \
           ${TENSORFLOW_LITE_SRC};branch=${SRCBRANCH_tf};name=tf;destsuffix=tfgit \
           file://0001-Findtim-vx.cmake-Fix-LIBDIR-for-multilib-environment.patch \
"

S = "${WORKDIR}/git"

inherit python3native cmake

EXTRA_OECMAKE = "-DCMAKE_SYSROOT=${PKG_CONFIG_SYSROOT_DIR}"
EXTRA_OECMAKE += " \
     -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
     -DTIM_VX_INSTALL=${STAGING_DIR_HOST}/usr \
     -DFETCHCONTENT_SOURCE_DIR_TENSORFLOW=${WORKDIR}/tfgit \
     -DTFLITE_LIB_LOC=${STAGING_DIR_HOST}${libdir}/libtensorflow-lite.so \
     ${S} \
"

CXXFLAGS += "-fPIC"

do_configure[network] = "1"
do_configure:prepend() {
    export HTTP_PROXY=${http_proxy}
    export HTTPS_PROXY=${https_proxy}
    export http_proxy=${http_proxy}
    export https_proxy=${https_proxy}

    # There is no Fortran compiler in the toolchain, but bitbake sets this variable anyway
    # with unavailable binary.
    export FC=""
}

do_install() {
    # install libraries
    install -d ${D}${libdir}
    for lib in ${B}/lib*.so*
    do
        cp --no-preserve=ownership -d $lib ${D}${libdir}
    done

    # install header files
    install -d ${D}${includedir}/tensorflow-lite-vx-delegate
    cd ${S}
    cp --parents \
        $(find . -name "*.h*") \
        ${D}${includedir}/tensorflow-lite-vx-delegate

}

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# Output library is unversioned
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

COMPATIBLE_MACHINE          = "(^$)"
COMPATIBLE_MACHINE:imxgpu3d = "(mx8-nxp-bsp)"
COMPATIBLE_MACHINE:mx8mm-nxp-bsp    = "(^$)"
