using System;
using System.Collections.ObjectModel;
using System.Linq;
using Firebase.Database;
using Firebase.Database.Query;
using foursquare_neophyte.Models;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace foursquare_neophyte.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class HomePage : ContentPage
    {
        private static string daysNodeUrl = $"{Constants.FirebaseUrl}";
        ObservableCollection<DateDisplayModel> Dates = new ObservableCollection<DateDisplayModel>();
        FirebaseClient firebaseClient = new FirebaseClient(daysNodeUrl);


        public HomePage()
        {
            InitializeComponent();

            Title = "Foursquare Newcomers";
            if (Device.RuntimePlatform == Device.Android)
            {
                absContent.BackgroundColor = Color.FromHex("#FAFAFA");
            }
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();

            // clear existing data
            Dates.Clear();

            var dates = firebaseClient
                .Child("members")
                .OrderByKey()
                .LimitToLast(25)
                .OnceAsync<object>()
                .Result
                .OrderByDescending(x => x.Key)
                .Select(x => x.Key); ;

            foreach (var date in dates)
            {
                var displayModel = new DateDisplayModel();
                displayModel.Date = date;
                displayModel.DateDetails = (DateTime.Parse(date)).ToString("ddd, dd MMM yyy");
                Dates.Add(displayModel);
            }

            this.lstDates.ItemsSource = Dates;
        }

        protected async void AddRecord(object sender, EventArgs e)
        {

        }
    }
}
