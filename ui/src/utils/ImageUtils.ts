const convertImageToBlob = (imageBlob: Blob): Promise<Blob> => {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => {
      const canvas = document.createElement("canvas");
      canvas.width = img.width;
      canvas.height = img.height;

      const ctx = canvas.getContext("2d");
      if (!ctx) {
        reject(new Error("Failed to get canvas context"));
        return;
      }

      ctx.drawImage(img, 0, 0);

      canvas.toBlob((blob) => {
        if (blob) {
          resolve(blob);
        } else {
          reject(new Error("Failed to convert image to blob"));
        }
      }, "image/png");
    };
    img.onerror = () => {
      reject(new Error("Failed to load image"));
    };
    img.src = URL.createObjectURL(imageBlob);
  });
};

export { convertImageToBlob };
