interface ImageDisplay {
  id: number;
  displayName: string;
  isPrivate: boolean;
  createTime: string;
  thumbnailUrl: string;
  externalUrl?: string;
}

interface ImageView {
  imageId: number;
  src: string;
  alt: string;
  "data-source": string;
}

export type { ImageDisplay, ImageView };