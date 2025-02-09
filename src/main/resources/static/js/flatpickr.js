 let maxDate = new Date();
 maxDate = maxDate.setMonth(maxDate.getMonth() + 3);
 
 flatpickr('#checkinDate', {
//   mode: "range",
   locale: 'ja',
   minDate: 'today',
   maxDate: maxDate
 });