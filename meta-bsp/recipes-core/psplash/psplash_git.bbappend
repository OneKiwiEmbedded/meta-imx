FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://psplash-basic.service \
    file://psplash-network.service \
    file://psplash-quit.service \
    file://0001-psplash-Change-colors-for-the-Variscite-Yocto-logo.patch \
	file://psplash-bar.png \
"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' psplash-basic.service psplash-network.service psplash-quit.service', '', d)}"

SPLASH_IMAGES = "file://psplash-poky.png;outsuffix=default"

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/psplash-basic.service ${D}/${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/psplash-network.service ${D}/${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/psplash-quit.service ${D}/${systemd_unitdir}/system
    fi
}

do_configure:prepend() {
	cp ${WORKDIR}/*.png ${S}/base-images
}

INITSCRIPT_PARAMS = "start 0 S . stop 21 0 1 6 ."