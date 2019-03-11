using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace foursquare_neophyte.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class HomePage : ContentPage
    {
        public HomePage()
        {
            InitializeComponent();

            Title = "Foursqaure Newcomers";
        }
    }
}
