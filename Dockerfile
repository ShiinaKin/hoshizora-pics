FROM ubuntu:noble AS build-imagemagick

WORKDIR /imagemagick

RUN apt update && \
    apt install --no-install-recommends -y \
        git curl ca-certificates build-essential pkg-config libtool \
        libtool libltdl-dev \
        zlib1g-dev libbz2-dev liblzma-dev libzstd-dev \
        libfontconfig-dev libfreetype-dev libxml2-dev \
        libjpeg-dev libpng-dev libwebp-dev libtiff-dev \
        libheif-dev libjbig-dev libjxl-dev \
        liblcms2-dev liblqr-1-0-dev \
        libopenexr-dev libopenjp2-7-dev libraw-dev \
        libdjvulibre-dev libpango1.0-dev libraqm-dev libwmf-dev libzip-dev
RUN git clone --depth 1 --branch 7.1.2-18 https://github.com/ImageMagick/ImageMagick.git ImageMagick

RUN cd ImageMagick \
          && ./configure --prefix=/opt/imagemagick --with-modules --disable-docs --without-x \
          && make -j"$(nproc)" \
          && make install \
          && ldconfig /opt/imagemagick/lib

FROM eclipse-temurin:25-jdk-noble AS build-app

COPY --from=build-imagemagick /opt/imagemagick /opt/imagemagick
RUN apt update && apt install --no-install-recommends -y \
      libgomp1 liblcms2-2 libraqm0 liblqr-1-0 libxml2 libltdl7 libjbig0 libtiff6 \
      libheif1 libwebpmux3 libwebpdemux2 libwebp7 libopenexr-3-1-30 libzip4 \
      libwmflite-0.2-7 libdjvulibre21 libopenjp2-7 libjpeg-turbo8 libpangocairo-1.0-0 \
      libpango-1.0-0 libglib2.0-0t64 libcairo2 libraw23t64 libjxl0.7 \
   && rm -rf /var/lib/apt/lists/*
RUN ldd /opt/imagemagick/lib/libMagickWand-7.Q16HDRI.so

WORKDIR /hoshizora-pics

COPY gradlew gradle.properties build.gradle.kts settings.gradle.kts ./
COPY .git .git
COPY gradle gradle
COPY app app
COPY ui ui

RUN chmod +x ./gradlew

ENV CI=true
ENV MAGICK_WAND_LIB_PATH=/opt/imagemagick/lib/libMagickWand-7.Q16HDRI.so

RUN ./gradlew clean build

FROM eclipse-temurin:25-jre-noble
LABEL maintainer="ShiinaKin <shiina@sakurasou.io>"
WORKDIR /hoshizora-pics

COPY --from=build-imagemagick /opt/imagemagick /opt/imagemagick
RUN apt update && apt install --no-install-recommends -y \
      libgomp1 liblcms2-2 libraqm0 liblqr-1-0 libxml2 libltdl7 libjbig0 libtiff6 \
      libheif1 libwebpmux3 libwebpdemux2 libwebp7 libopenexr-3-1-30 libzip4 \
      libwmflite-0.2-7 libdjvulibre21 libopenjp2-7 libjpeg-turbo8 libpangocairo-1.0-0 \
      libpango-1.0-0 libglib2.0-0t64 libcairo2 libraw23t64 libjxl0.7 \
   && rm -rf /var/lib/apt/lists/*
RUN ldd /opt/imagemagick/lib/libMagickWand-7.Q16HDRI.so

COPY --from=build-app /hoshizora-pics/build/*.jar hoshizora-pics.jar

VOLUME /hoshizora-pics/images

ENV MAGICK_WAND_LIB_PATH=/opt/imagemagick/lib/libMagickWand-7.Q16HDRI.so
ENV JVM_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["bash", "-c", "java $JVM_OPTS --enable-native-access=ALL-UNNAMED -jar hoshizora-pics.jar"]
