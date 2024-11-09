const transToDirect = (fileName: string, url: string) => {
  return url;
};

const transToMarkdown = (fileName: string, url: string) => {
  return `![${fileName}](${url})`;
};

const transToMarkdownWithLink = (fileName: string, url: string) => {
  return `[![${fileName}](${url})](${url})`;
};

const transToHTML = (fileName: string, url: string) => {
  return `<img src="${url}" alt="${fileName}" />`;
};

const transToBBCode = (fileName: string, url: string) => {
  return `[img]${url}[/img]`;
};

export { transToDirect, transToMarkdown, transToMarkdownWithLink, transToHTML, transToBBCode };
