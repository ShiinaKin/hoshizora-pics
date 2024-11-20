import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);

const formatUTCStringToLocale = (utcString: string | undefined, format: string = "YYYY/MM/DD HH:mm:ss") => {
  return dayjs(utcString).utc(true).local().format(format);
};

export { formatUTCStringToLocale };
