using foursquare_neophyte.Views;
using Xamarin.Forms;

namespace foursquare_neophyte
{
    public partial class App : Application
    {
        public App()
        {
            InitializeComponent();

            // customize the navbar
            var nav = new NavigationPage(new HomePage()) {BarBackgroundColor = Color.FromHex("#92278F")};

            if (Device.RuntimePlatform == Device.iOS)
            {
                nav.BarTextColor = Color.Black;
            }

            // set the signin page as the main page
            MainPage = nav;

        }

        protected override void OnStart()
        {
            // Handle when your app starts
        }

        protected override void OnSleep()
        {
            // Handle when your app sleeps
        }

        protected override void OnResume()
        {
            // Handle when your app resumes
        }
    }
}
